import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Bell } from "lucide-react";
import { useNotificationStore } from "../store/useNotificationStore";
import { useChatStore } from "../store/useChatStore";
import { useAuthStore } from "../store/useAuthStore";

const formatTime = (iso) => {
  if (!iso) return "";
  try {
    const d = new Date(iso);
    return d.toLocaleString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
      day: "2-digit",
      month: "2-digit",
    });
  } catch {
    return "";
  }
};

const NotificationBell = () => {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  const { authUser } = useAuthStore();
  const authUserId = authUser?.userId || authUser?._id;

  const {
    notifications,
    unreadCount,
    isLoading,
    fetchNotifications,
    markRead,
    markAllRead,
  } = useNotificationStore();
  const { users, setSelectedUser, getUsers } = useChatStore();

  useEffect(() => {
    const onDoc = (e) => {
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    };
    document.addEventListener("mousedown", onDoc);
    return () => document.removeEventListener("mousedown", onDoc);
  }, []);

  useEffect(() => {
    if (open) {
      fetchNotifications(0);
    }
  }, [open, fetchNotifications]);

  const handleOpenChat = async (n) => {
    if (!n.conversationId) {
      setOpen(false);
      return;
    }
    if (!n.read) await markRead(n.id);

    let peer = users?.find((u) => String(u.id) === String(n.conversationId));
    if (!peer && authUserId) {
      await getUsers();
      peer = useChatStore.getState().users?.find(
        (u) => String(u.id) === String(n.conversationId)
      );
    }
    if (peer) setSelectedUser(peer);

    setOpen(false);
    navigate("/");
  };

  return (
    <div className="relative" ref={ref}>
      <button
        type="button"
        className="btn btn-sm btn-ghost gap-1 relative"
        aria-label="Thông báo"
        onClick={() => setOpen((o) => !o)}
      >
        <Bell className="w-5 h-5" />
        {unreadCount > 0 && (
          <span className="absolute -top-0.5 -right-0.5 min-w-[1.1rem] h-[1.1rem] rounded-full bg-rose-500 text-[10px] text-white flex items-center justify-center font-bold">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-[min(100vw-2rem,22rem)] max-h-[min(70vh,24rem)] overflow-hidden rounded-2xl border border-base-300 bg-base-100 shadow-xl z-50 flex flex-col">
          <div className="flex items-center justify-between px-4 py-3 border-b border-base-300 bg-base-200/50">
            <span className="font-semibold text-sm">Thông báo</span>
            {unreadCount > 0 && (
              <button
                type="button"
                className="text-xs text-primary hover:underline"
                onClick={() => markAllRead()}
              >
                Đọc hết
              </button>
            )}
          </div>

          <div className="overflow-y-auto flex-1">
            {isLoading && notifications.length === 0 && (
              <p className="p-4 text-sm text-base-content/60">Đang tải…</p>
            )}
            {!isLoading && notifications.length === 0 && (
              <p className="p-4 text-sm text-base-content/60">Chưa có thông báo</p>
            )}
            {notifications.map((n) => (
              <button
                key={n.id}
                type="button"
                className={`w-full text-left px-4 py-3 border-b border-base-200 last:border-0 hover:bg-base-200/70 transition-colors ${
                  !n.read ? "bg-primary/5" : ""
                }`}
                onClick={() => {
                  if (n.type === "NEW_MATCH" || n.type === "NEW_MESSAGE") {
                    handleOpenChat(n);
                  } else if (!n.read) {
                    markRead(n.id);
                  }
                }}
              >
                <div className="flex justify-between gap-2 items-start">
                  <span className="font-medium text-sm line-clamp-2">{n.title}</span>
                  <span className="text-[10px] text-base-content/50 shrink-0">
                    {formatTime(n.createdAt)}
                  </span>
                </div>
                <p className="text-xs text-base-content/70 mt-1 line-clamp-2">{n.body}</p>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
