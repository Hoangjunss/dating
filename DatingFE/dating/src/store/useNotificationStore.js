import { create } from "zustand";
import toast from "react-hot-toast";
import { axiosInstance } from "../lib/axios";
import { useChatStore } from "./useChatStore";

let stompSubscription = null;

const shouldToastPayload = (payload) => {
  if (payload?.type !== "NEW_MESSAGE") return true;
  const path = window.location.pathname;
  const { selectedUser } = useChatStore.getState();
  if (
    path === "/" &&
    payload.conversationId &&
    selectedUser?.id &&
    String(selectedUser.id) === String(payload.conversationId)
  ) {
    return false;
  }
  return true;
};

const mergeIncoming = (list, incoming) => {
  const id = incoming?.id;
  if (!id) return [incoming, ...list];
  const idx = list.findIndex((n) => String(n.id) === String(id));
  if (idx === -1) return [incoming, ...list];
  const next = [...list];
  next[idx] = { ...next[idx], ...incoming };
  return next;
};

export const useNotificationStore = create((set, get) => ({
  notifications: [],
  unreadCount: 0,
  isLoading: false,

  setUnreadCount: (n) => set({ unreadCount: typeof n === "number" ? n : 0 }),

  fetchUnreadCount: async () => {
    try {
      const res = await axiosInstance.get("/notifications/unread-count");
      set({ unreadCount: res.data?.count ?? 0 });
    } catch {
      /* ignore */
    }
  },

  fetchNotifications: async (page = 0) => {
    set({ isLoading: true });
    try {
      const res = await axiosInstance.get("/notifications", {
        params: { page, size: 30, sort: "createdAt,desc" },
      });
      const content = res.data?.content ?? res.data ?? [];
      set({ notifications: Array.isArray(content) ? content : [] });
      get().fetchUnreadCount();
    } catch {
      toast.error("Không tải được thông báo");
    } finally {
      set({ isLoading: false });
    }
  },

  /** Gọi sau khi STOMP client đã connected */
  attachStomp: (stompClient) => {
    stompSubscription?.unsubscribe();
    stompSubscription = null;
    if (!stompClient) return;

    stompSubscription = stompClient.subscribe("/user/queue/notifications", (frame) => {
      let payload;
      try {
        payload = JSON.parse(frame.body);
      } catch {
        return;
      }

      set((state) => ({
        notifications: mergeIncoming(state.notifications, payload),
      }));

      if (!payload.read && shouldToastPayload(payload)) {
        const line = payload.title ? `${payload.title}: ${payload.body}` : payload.body;
        toast(line, { icon: "🔔" });
      }

      get().fetchUnreadCount();
    });
  },

  detachStomp: () => {
    stompSubscription?.unsubscribe();
    stompSubscription = null;
  },

  markRead: async (id) => {
    try {
      await axiosInstance.patch(`/notifications/${id}/read`);
      set((state) => ({
        notifications: state.notifications.map((n) =>
          String(n.id) === String(id) ? { ...n, read: true } : n
        ),
      }));
      get().fetchUnreadCount();
    } catch {
      toast.error("Không cập nhật được trạng thái");
    }
  },

  markAllRead: async () => {
    try {
      await axiosInstance.post("/notifications/read-all");
      set((state) => ({
        notifications: state.notifications.map((n) => ({ ...n, read: true })),
        unreadCount: 0,
      }));
    } catch {
      toast.error("Không đánh dấu đã đọc được");
    }
  },
}));
