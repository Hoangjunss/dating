import { useEffect, useState } from "react";
import { useChatStore } from "../store/useChatStore";
import { useAuthStore } from "../store/useAuthStore";
import SidebarSkeleton from "./skeletons/SidebarSkeleton";
import { Users, Search, Heart } from "lucide-react";

const Sidebar = () => {
  const { getUsers, users, selectedUser, setSelectedUser, isUsersLoading } = useChatStore();
  const { authUser } = useAuthStore();
  const [showOnlineOnly, setShowOnlineOnly] = useState(false);

  useEffect(() => {
    getUsers(authUser.userId);
  }, [getUsers, authUser.userId]);
  console.log("Users in sidebar:", users);
  

  const filteredUsers = showOnlineOnly
    ? users.filter((user) => authUser.userId !== user.id && user.status === "online")
    : users.filter((user) => authUser.userId !== user.id);

  if (isUsersLoading) return <SidebarSkeleton />;

  return (
    <aside className="h-full flex flex-col">
      {/* Header Sidebar - Sạch sẽ, tông màu hồng nhạt */}
      <div className="p-6 border-b border-rose-100 bg-rose-50/50">
        <div className="flex items-center gap-3 mb-6">
          <div className="bg-rose-500 p-2 rounded-xl shadow-lg shadow-rose-200">
            <Heart className="size-6 text-white fill-white" />
          </div>
          <span className="text-xl font-black text-rose-950 tracking-tighter italic">Lovera Chats</span>
        </div>

        {/* Thanh tìm kiếm giả lập (Search bar) */}
        <div className="relative mb-5 group">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 size-5 text-rose-300 transition-colors group-focus-within:text-rose-500" />
          <input 
            type="text" 
            placeholder="Tìm người thương..." 
            className="w-full bg-white pl-12 pr-4 py-3 rounded-full border border-rose-100 outline-none text-sm placeholder:text-rose-300 focus:border-rose-300 focus:ring-2 focus:ring-rose-100 transition"
          />
        </div>

        {/* Online filter - Thiết kế lại cho gọn */}
        <div className="flex items-center justify-between gap-2 text-sm">
          <label className="flex items-center gap-2 cursor-pointer font-semibold text-rose-900">
            <input
              type="checkbox"
              checked={showOnlineOnly}
              onChange={(e) => setShowOnlineOnly(e.target.checked)}
              className="checkbox checkbox-sm checkbox-secondary [--chkbg:theme(colors.rose.500)] [--chkfg:white] border-rose-200"
            />
            Chỉ hiện online
          </label>
          <span className="text-xs text-rose-400 font-bold">({filteredUsers.length} người)</span>
        </div>
      </div>

      {/* Danh sách người dùng */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2 selection:bg-rose-100">
        {filteredUsers.map((user) => (
          <button
            key={user.id}
            onClick={() => setSelectedUser(user.id)}
            className={`
              w-full p-4 flex items-center gap-4 rounded-2xl transition-all duration-300
              ${selectedUser?.id === user.id
                ? "bg-rose-100 border-rose-200 shadow-inner shadow-rose-200/50"
                : "hover:bg-rose-50 border-transparent hover:border-rose-100"
              }
              border
            `}
          >
            {/* Avatar và trạng thái online */}
            <div className="relative mx-auto md:mx-0 flex-shrink-0">
              <img
                src={user.profilePic || "/avatar.png"}
                alt={user.fullName}
                className="size-14 rounded-full object-cover border-2 border-white shadow-md transition-transform duration-500"
              />
              {user.status === "online" && (
                <span className="absolute bottom-0 right-0 size-4.5 bg-green-500 rounded-full border-2 border-white shadow-lg animate-pulse" />
              )}
            </div>

            {/* Thông tin người dùng - Chỉ hiện trên màn hình lớn */}
            <div className="hidden md:block text-left flex-1 min-w-0">
              <div className="font-bold text-base text-rose-950 truncate">{user.fullName}</div>
              <div className="text-xs text-rose-400 font-medium truncate">
                {user.status === "online" ? "Đang online" : "Ngoại tuyến"}
              </div>
            </div>
          </button>
        ))}

        {filteredUsers.length === 0 && (
          <div className="text-center text-rose-300 py-10 px-4 text-sm italic">
            Hơi vắng vẻ nhỉ? Thử tìm thêm bạn mới xem!
          </div>
        )}
      </div>
    </aside>
  );
};
export default Sidebar;