import { X, MapPin, CheckCircle } from "lucide-react";
import { useAuthStore } from "../store/useAuthStore";
import { useChatStore } from "../store/useChatStore";

const ChatHeader = () => {
  const { selectedUser, setSelectedUser } = useChatStore();
  const { onlineUsers } = useAuthStore();

  if (!selectedUser) return null;

  const isOnline = onlineUsers.map(String).includes(String(selectedUser.id));

  return (
    <div className="p-5 border-b border-rose-100 bg-white sticky top-0 z-10">
      <div className="flex items-center justify-between gap-4">
        {/* Thông tin người đang chat */}
        <div className="flex items-center gap-4">
          {/* Avatar */}
          <div className="relative">
            <img 
              src={selectedUser.profilePic || "/avatar.png"} 
              alt={selectedUser.fullName} 
              className="size-16 rounded-full object-cover border-4 border-rose-50 shadow-inner"
            />
          </div>

          {/* Tên và trạng thái */}
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <h3 className="text-xl font-extrabold text-rose-950 truncate">{selectedUser.fullName}</h3>
              <CheckCircle className="text-blue-500 fill-blue-50" size={20} />
            </div>
            
            <div className="flex items-center gap-1.5 text-xs text-rose-400 font-medium">
               <MapPin size={14} className="text-rose-400" />
               <span>{selectedUser.location || "Việt Nam"}</span>
               <span className="mx-1">•</span>
               {isOnline ? (
                   <span className="text-green-600 font-bold">Trực tuyến</span>
               ) : (
                   <span className="text-rose-300">Ngoại tuyến</span>
               )}
            </div>
          </div>
        </div>

        {/* Nút đóng chat - Thiết kế gọn gàng */}
        <button 
          onClick={() => setSelectedUser(null)}
          className="p-3 bg-rose-50 text-rose-400 rounded-2xl hover:bg-rose-100 hover:text-rose-600 transition-colors border border-rose-100 active:scale-95 shadow-sm"
        >
          <X size={20} />
        </button>
      </div>
    </div>
  );
};
export default ChatHeader;