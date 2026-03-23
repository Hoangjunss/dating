import { useChatStore } from "../store/useChatStore";
import { useEffect, useRef } from "react";

import ChatHeader from "./ChatHeader";
import MessageInput from "./MessageInput";
import MessageSkeleton from "./skeletons/MessageSkeleton";
import { useAuthStore } from "../store/useAuthStore";
import { formatMessageTime } from "../lib/utils";
import { BadgeCheck } from "lucide-react";

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
    getMessages(selectedUser,authUser.userId);
    subscribeToMessages();
    return () => unsubscribeFromMessages();
  }, [selectedUser, authUser.userId, getMessages, subscribeToMessages, unsubscribeFromMessages]);
  console.log("Messages in chat container:", selectedUser);

  useEffect(() => {
    if (messageEndRef.current && messages) {
      messageEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  if (isMessagesLoading) {
    return (
      <div className="flex-1 flex flex-col overflow-auto bg-rose-50/20">
        <ChatHeader />
        <div className="flex-1 p-6 space-y-6">
          <MessageSkeleton />
        </div>
        <MessageInput />
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col h-full bg-rose-50/20">
      <ChatHeader />

      {/* Khung tin nhắn - Nền hồng cực nhạt */}
      <div className="flex-1 overflow-y-auto p-6 md:p-8 space-y-8 selection:bg-rose-200">
        {messages.map((message) => {
          const isMyMessage = message.senderId === authUser.userId;
          
          return (
            <div
              key={message.id}
              className={`flex ${isMyMessage ? "justify-end" : "justify-start"} items-end gap-3`}
            >
              {/* Avatar người gửi (nếu không phải tin nhắn của tôi) */}
              {!isMyMessage && (
                <img
                  src={selectedUser.profilePic || "/avatar.png"}
                  alt="Avatar"
                  className="size-10 rounded-full object-cover border-2 border-white shadow-md flex-shrink-0"
                />
              )}
              
              <div className={`flex flex-col ${isMyMessage ? "items-end" : "items-start"} max-w-[70%] md:max-w-[60%]`}>
                {/* Bong bóng tin nhắn */}
                <div
                  className={`
                    p-4 rounded-[1.5rem] shadow-lg shadow-rose-100/50 relative group
                    ${isMyMessage 
                      ? "bg-gradient-to-br from-rose-500 to-rose-400 text-white rounded-br-lg" 
                      : "bg-white text-rose-950 rounded-bl-lg border border-rose-100/50"
                    }
                  `}
                >
                  {/* Nội dung hình ảnh */}
                  {message.image && (
                    <img
                      src={message.image}
                      alt="Attachment"
                      className="max-w-[250px] md:max-w-[300px] rounded-xl mb-3 border-4 border-white shadow-inner"
                    />
                  )}
                  {/* Nội dung văn bản */}
                  {message.content && <p className="text-sm md:text-base leading-relaxed font-medium">{message.content}</p>}
                </div>

                {/* Thời gian gửi tin nhắn - Chỉ hiện khi hover */}
                <span className={`text-[10px] text-rose-300 font-medium mt-1.5 px-2 transition-opacity duration-300 opacity-0 group-hover:opacity-100 flex items-center gap-1`}>
                  {formatMessageTime(message.createdAt)}
                  {isMyMessage && <BadgeCheck size={12} className="text-rose-300" />}
                </span>
              </div>

              {/* Avatar của tôi (nếu là tin nhắn của tôi) */}
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
        {/* Phần tử mốc để tự động cuộn */}
        <div ref={messageEndRef} />
      </div>

      <MessageInput />
    </div>
  );
};
export default ChatContainer;