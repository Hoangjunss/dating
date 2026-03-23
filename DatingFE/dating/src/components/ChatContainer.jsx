// ChatContainer.jsx
import { useChatStore } from "../store/useChatStore";
import { useEffect, useRef } from "react";
import ChatHeader from "./ChatHeader";
import MessageInput from "./MessageInput";
import MessageSkeleton from "./skeletons/MessageSkeleton";
import { useAuthStore } from "../store/useAuthStore";
import { formatMessageTime } from "../lib/utils";
import { BadgeCheck, AlertCircle } from "lucide-react";
import PhotoBubble from "./PhotoBubbleChat";

const ChatContainer = () => {
  const {
    messages,
    getMessages,
    isMessagesLoading,
    selectedUser,
    subscribeToMessages,
    unsubscribeFromMessages,
  } = useChatStore();
  const { authUser } = useAuthStore();
  const messageEndRef = useRef(null);

  useEffect(() => {
    getMessages(selectedUser, authUser.userId);
    subscribeToMessages();
    return () => unsubscribeFromMessages();
  }, [selectedUser, authUser.userId, getMessages, subscribeToMessages, unsubscribeFromMessages]);

  useEffect(() => {
    if (messageEndRef.current && messages) {
      messageEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  if (isMessagesLoading) {
    return (
      <div className="flex-1 flex flex-col overflow-auto bg-rose-50/20">
        <ChatHeader />
        <div className="flex-1 p-6 space-y-6"><MessageSkeleton /></div>
        <MessageInput />
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col h-full bg-rose-50/20">
      <ChatHeader />

      <div className="flex-1 overflow-y-auto p-6 md:p-8 space-y-8 selection:bg-rose-200">
        {messages.map((message) => {
          const isMyMessage = message.senderId === authUser.userId;

          return (
            <div
              key={message.id}
              className={`flex ${isMyMessage ? "justify-end" : "justify-start"} items-end gap-3`}
            >
              {!isMyMessage && (
                <img
                  src={selectedUser.profilePic || "/avatar.png"}
                  alt="Avatar"
                  className="size-10 rounded-full object-cover border-2 border-white shadow-md flex-shrink-0"
                />
              )}

              {/* ── Bọc cả bubble + time trong group để hover hoạt động ── */}
              <div className={`group flex flex-col ${isMyMessage ? "items-end" : "items-start"} max-w-[70%] md:max-w-[60%]`}>
                <div
                  className={`
                    rounded-[1.5rem] shadow-lg shadow-rose-100/50 relative overflow-hidden
                    ${message.type === "PHOTO"
                      ? "p-1 bg-transparent shadow-none"  // ảnh không cần padding/bg
                      : `p-4 ${isMyMessage
                          ? "bg-gradient-to-br from-rose-500 to-rose-400 text-white rounded-br-lg"
                          : "bg-white text-rose-950 rounded-bl-lg border border-rose-100/50"
                        }`
                    }
                  `}
                >
                  {/* PHOTO */}
                  {message.type === "PHOTO" && message.photo && (
                    <PhotoBubble message={message} isMyMessage={isMyMessage} />
                  )}

                  {/* TEXT */}
                  {message.type === "TEXT" && message.content && (
                    <p className="text-sm md:text-base leading-relaxed font-medium">
                      {message.content}
                    </p>
                  )}

                  {/* Tin nhắn đã thu hồi */}
                  {message.unsent && (
                    <p className="text-sm italic opacity-60">Tin nhắn đã bị thu hồi</p>
                  )}
                </div>

                {/* Thời gian — hiện khi hover vào group */}
                <span className="text-[10px] text-rose-300 font-medium mt-1.5 px-2 transition-opacity duration-300 opacity-0 group-hover:opacity-100 flex items-center gap-1">
                  {formatMessageTime(message.sentAt)}  {/* ✅ sentAt thay vì createdAt */}
                  {isMyMessage && <BadgeCheck size={12} className="text-rose-300" />}
                </span>
              </div>

              {isMyMessage && (
                <img
                  src={authUser.profilePic || "/avatar.png"}
                  alt="Avatar"
                  className="size-10 rounded-full object-cover border-2 border-white shadow-md flex-shrink-0"
                />
              )}
            </div>
          );
        })}
        <div ref={messageEndRef} />
      </div>

      <MessageInput />
    </div>
  );
};

export default ChatContainer;