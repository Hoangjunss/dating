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

  getUsers: async (userId) => {
    set({ isUsersLoading: true });
    try {
      const res = await axiosInstance.get(`/conversations/user/${userId}`);
      set({ users: res.data.content });
    } catch (error) {
      toast.error(error.response.data.message);
    } finally {
      set({ isUsersLoading: false });
    }
  },

  getMessages: async (conversationId, viewerId) => {
    set({ isMessagesLoading: true });
    try {
      const res = await axiosInstance.get(`/messages/${conversationId}`, {
        params: { viewerId }  
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
    const { authUser } = useAuthStore.getState();
    const payload = {
    conversationId: selectedUser, // Lấy ID cuộc hội thoại
    senderId: authUser.userId,                   // ID của chính bạn (người gửi)
    content: messageData.content                // Nội dung tin nhắn
  };
    try {
      const res = await axiosInstance.post('/messages/send', payload);
      set({ messages: [...messages, res.data] });
    } catch (error) {
      toast.error(error.response.data.message);
    }
  },
  // useChatStore.js
  // useChatStore.js — chỉ fix phần sendPhoto
sendPhoto: async (file) => {
    const { selectedUser } = get();
    const { authUser } = useAuthStore.getState();

    const blobUrl = URL.createObjectURL(file);
    const tempId  = `temp-${Date.now()}`;

    const tempMessage = {
      id:             tempId,
      senderId:       authUser.userId,
      conversationId: selectedUser,
      content:        null,
      type:           "PHOTO",
      photo:          blobUrl,
      seen:           false,
      status:         "uploading",
      sentAt:         new Date().toISOString(),
      unsent:         false,
    };

    set({ messages: [...get().messages, tempMessage] });

    const formData = new FormData();
    formData.append("conversationId", selectedUser);
    formData.append("senderId", authUser.userId);
    formData.append("photo", file);

    try {
      const res = await axiosInstance.post("/messages/photo", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      // ✅ REPLACE tempMessage bằng data thật — không add thêm
      set({
        messages: get().messages.map((msg) =>
          msg.id === tempId ? { ...res.data, status: "sent" } : msg
        ),
      });
    } catch (error) {
      // ✅ Đánh dấu failed — không xóa, cho user biết lỗi
      set({
        messages: get().messages.map((msg) =>
          msg.id === tempId ? { ...msg, status: "failed" } : msg
        ),
      });
      toast.error(error.response?.data?.message || "Lỗi gửi ảnh");
    } finally {
      URL.revokeObjectURL(blobUrl); // giải phóng bộ nhớ
    }
  },

  subscribeToMessages: () => {
    const { selectedUser } = get();
    if (!selectedUser) return;

   // const socket = useAuthStore.getState().socket;

    /* socket.on("newMessage", (newMessage) => {
      const isMessageSentFromSelectedUser = newMessage.senderId === selectedUser._id;
      if (!isMessageSentFromSelectedUser) return;

      set({
        messages: [...get().messages, newMessage],
      });
    }); */
  },

  unsubscribeFromMessages: () => {
    /* const socket = useAuthStore.getState().socket;
    socket.off("newMessage"); */
  },

  setSelectedUser: (selectedUser) => set({ selectedUser }),
}));