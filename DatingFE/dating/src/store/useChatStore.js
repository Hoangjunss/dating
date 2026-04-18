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
        params: { viewerId },
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

    const wsBody = JSON.stringify({
      conversationId: selectedUser.id,
      content: messageData.content,
    });

    if (stompClient && stompConnected) {
      stompClient.publish({
        destination: "/app/chat.send",
        body: wsBody,
      });
      return;
    }

    const payload = {
      conversationId: selectedUser.id,
      senderId: authUser.userId,
      content: messageData.content,
    };

    try {
      const res = await axiosInstance.post("/messages/send", payload);
      set({ messages: [...messages, res.data] });
    } catch (error) {
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
        const alreadyExists = state.messages.some(
          (m) => String(m.id) === String(newMessage.id)
        );
        if (alreadyExists) return state;
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
