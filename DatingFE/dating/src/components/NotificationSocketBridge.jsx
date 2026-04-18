import { useEffect } from "react";
import { useAuthStore } from "../store/useAuthStore";
import { useNotificationStore } from "../store/useNotificationStore";

/**
 * Đăng ký queue STOMP /user/queue/notifications và đồng bộ số chưa đọc khi đã đăng nhập.
 */
const NotificationSocketBridge = () => {
  const authUser = useAuthStore((s) => s.authUser);
  const stompClient = useAuthStore((s) => s.stompClient);
  const stompConnected = useAuthStore((s) => s.stompConnected);
  const attachStomp = useNotificationStore((s) => s.attachStomp);
  const detachStomp = useNotificationStore((s) => s.detachStomp);
  const fetchUnreadCount = useNotificationStore((s) => s.fetchUnreadCount);

  useEffect(() => {
    if (authUser && stompClient && stompConnected) {
      attachStomp(stompClient);
      fetchUnreadCount();
    }
    return () => {
      detachStomp();
    };
  }, [authUser, stompClient, stompConnected, attachStomp, detachStomp, fetchUnreadCount]);

  return null;
};

export default NotificationSocketBridge;
