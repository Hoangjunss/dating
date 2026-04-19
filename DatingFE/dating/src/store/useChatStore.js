import { create } from "zustand";
import toast from "react-hot-toast";
import { axiosInstance } from "../lib/axios";
import { useAuthStore } from "./useAuthStore";

export const useChatStore = create((set, get) => ({
  messages: [],
  users: [],
  selectedUser: null,
  isUsersLoading: false,
  isMessagesLoading: false,
  conversationSub: null,

  getUsers: async () => {
    set({ isUsersLoading: true });
    try {
      const res = await axiosInstance.get("/conversations/me");
      const raw = Array.isArray(res.data) ? res.data : res.data?.content ?? [];
      set({
        users: raw.map((c) => ({
          id: c.id,
          fullName: c.nickName || "Trò chuyện",
          profilePic: null,
          status: "offline",
        })),
      });
    } catch (error) {
      toast.error(error.response?.data?.message || "Không tải được danh sách hội thoại");
    } finally {
      set({ isUsersLoading: false });
    }
  },

  getMessages: async (conversationId, viewerId) => {
    set({ isMessagesLoading: true });
    try {
      const res = await axiosInstance.get(`/messages/${conversationId}`, {
        //params: { viewerId },
      });
      set({ messages: res.data });
    } catch (error) {
      toast.error(error.response?.data?.message || "Lỗi kết nối");
    } finally {
      set({ isMessagesLoading: false });
    }
  },

  sendMessage: async (messageData) => {
    const { selectedUser, messages } = get();
    const { authUser, stompClient, stompConnected } = useAuthStore.getState();

    if (!selectedUser?.id) return;

    // ── Bước 1: Thêm optimistic temp message vào state ngay lập tức ──
    const tempId = `temp-${Date.now()}`;
    const optimisticMsg = {
      id: tempId,
      senderId: authUser.userId,
      conversationId: selectedUser.id,
      content: messageData.content,
      type: "TEXT",
      seen: false,
      sentAt: new Date().toISOString(),
      unsent: false,
      _pending: true, // flag nội bộ để nhận biết là temp
    };
    set({ messages: [...messages, optimisticMsg] });

    // ── Bước 2: Gửi qua WebSocket nếu đang kết nối ──
    // Kiểm tra cả stompClient.connected (trạng thái thực) lẫn flag Zustand
    const isWsReady =
      stompClient && stompConnected && stompClient.connected;

    if (isWsReady) {
      try {
        stompClient.publish({
          destination: "/app/chat.send",
          body: JSON.stringify({
            conversationId: selectedUser.id,
            content: messageData.content,
          }),
        });
        // Không return → chờ subscription nhận về và replace temp
        // (xem subscribeToMessages bên dưới)
        return;
      } catch (error) {
        console.error("[WS] publish thất bại, fallback sang HTTP:", error);
        // Nếu WS crash → tiếp tục xuống HTTP fallback bên dưới
        // Giữ nguyên temp message trong state, HTTP sẽ replace nó
      }
    }

    // ── Bước 3: Fallback — gửi qua HTTP nếu WS không khả dụng ──
    console.log("[Chat] WebSocket không sẵn sàng, gửi qua HTTP");

    const payload = {
      conversationId: selectedUser.id,
      senderId: authUser.userId,
      content: messageData.content,
    };

    try {
      const res = await axiosInstance.post("/messages/send", payload);

      // Thay thế temp message bằng response thực từ server
      set((state) => ({
        messages: state.messages.map((msg) =>
          msg.id === tempId ? { ...res.data, _pending: false } : msg
        ),
      }));
    } catch (error) {
      // Đánh dấu temp message là failed thay vì xóa đi
      set((state) => ({
        messages: state.messages.map((msg) =>
          msg.id === tempId ? { ...msg, _failed: true, _pending: false } : msg
        ),
      }));
      toast.error(error.response?.data?.message || "Gửi tin nhắn thất bại");
    }
  },

  sendPhoto: async (file) => {
    const { selectedUser } = get();
    const { authUser } = useAuthStore.getState();

    if (!selectedUser?.id) return;

    const blobUrl = URL.createObjectURL(file);
    const tempId = `temp-${Date.now()}`;

    const tempMessage = {
      id: tempId,
      senderId: authUser.userId,
      conversationId: selectedUser.id,
      content: null,
      type: "PHOTO",
      photo: blobUrl,
      seen: false,
      status: "uploading",
      sentAt: new Date().toISOString(),
      unsent: false,
    };

    set({ messages: [...get().messages, tempMessage] });

    const formData = new FormData();
    formData.append("conversationId", selectedUser.id);
    formData.append("senderId", authUser.userId);
    formData.append("photo", file);

    try {
      const res = await axiosInstance.post("/messages/photo", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      set({
        messages: get().messages.map((msg) =>
          msg.id === tempId ? { ...res.data, status: "sent" } : msg
        ),
      });
    } catch (error) {
      set({
        messages: get().messages.map((msg) =>
          msg.id === tempId ? { ...msg, status: "failed" } : msg
        ),
      });
      toast.error(error.response?.data?.message || "Lỗi gửi ảnh");
    } finally {
      URL.revokeObjectURL(blobUrl);
    }
  },

  subscribeToMessages: () => {
    const { selectedUser } = get();
    if (!selectedUser?.id) return;

    get().conversationSub?.unsubscribe();

    const { stompClient, stompConnected } = useAuthStore.getState();
    if (!stompClient || !stompConnected) return;

    const topic = `/topic/conversation.${selectedUser.id}`;

    const sub = stompClient.subscribe(topic, (frame) => {
      let payload;
      try {
        payload = JSON.parse(frame.body);
      } catch {
        return;
      }

      const activeConversationId = get().selectedUser?.id;
      if (!activeConversationId) return;

      // ── Xử lý sự kiện UNSEND ──
      if (payload.type === "UNSEND") {
        if (String(payload.conversationId) !== String(activeConversationId)) return;
        set((state) => ({
          messages: state.messages.map((m) =>
            String(m.id) === String(payload.messageId)
              ? { ...m, content: null, unsent: true }
              : m
          ),
        }));
        return;
      }

      const newMessage = payload;
      if (String(newMessage.conversationId) !== String(activeConversationId)) return;

      set((state) => {
        // ── Check 1: Đã có message với ID thực này chưa (prevent duplicate) ──
        const alreadyExists = state.messages.some(
          (m) => !m._pending && String(m.id) === String(newMessage.id)
        );
        if (alreadyExists) return state;

        // ── Check 2: Tìm temp message của chính mình để replace ──
        // Điều kiện match: là temp message (_pending=true), cùng senderId, cùng content
        const { authUser } = useAuthStore.getState();
        const isMine =
          authUser &&
          String(newMessage.senderId) === String(authUser.userId);

        if (isMine) {
          const tempIndex = state.messages.findIndex(
            (m) =>
              m._pending === true &&
              String(m.senderId) === String(newMessage.senderId) &&
              m.content === newMessage.content
          );

          if (tempIndex !== -1) {
            // Replace temp message bằng message thực từ server
            const updated = [...state.messages];
            updated[tempIndex] = { ...newMessage, _pending: false };
            return { messages: updated };
          }
        }

        // ── Check 3: Message của người khác → thêm bình thường ──
        return { messages: [...state.messages, newMessage] };
      });
    });

    set({ conversationSub: sub });
  },

  unsubscribeFromMessages: () => {
    get().conversationSub?.unsubscribe();
    set({ conversationSub: null });
  },

  setSelectedUser: (selectedUser) => set({ selectedUser }),
}));