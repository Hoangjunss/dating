import { useState } from "react";
import { useAuthStore } from "../store/useAuthStore";
import { Camera, Mail, User, Calendar, ShieldCheck, BadgeCheck } from "lucide-react";

const ProfilePage = () => {
  const { authUser, isUpdatingProfile, updateProfile } = useAuthStore();
  const [selectedImg, setSelectedImg] = useState(null);

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = async () => {
      const base64Image = reader.result;
      setSelectedImg(base64Image);
      await updateProfile({ profilePic: base64Image });
    };
  };

  return (
    <div className="min-h-screen bg-[#FFF5F7] pt-28 pb-12 px-4">
      <div className="max-w-3xl mx-auto">
        {/* Card chính */}
        <div className="bg-white rounded-[2.5rem] shadow-xl shadow-rose-100/50 overflow-hidden border border-rose-50">
          
          {/* Header Profile với Gradient */}
          <div className="h-32 bg-gradient-to-r from-rose-400 to-orange-300 relative">
            <div className="absolute -bottom-16 left-1/2 -translate-x-1/2 md:left-12 md:translate-x-0">
              <div className="relative group">
                <img
                  src={selectedImg || authUser.profilePic || "/avatar.png"}
                  alt="Profile"
                  className="size-32 md:size-40 rounded-[2.5rem] object-cover border-8 border-white shadow-lg transition-transform duration-500 group-hover:scale-105"
                />
                <label
                  htmlFor="avatar-upload"
                  className={`
                    absolute bottom-2 -right-2 
                    bg-rose-500 hover:bg-rose-600
                    p-3 rounded-2xl cursor-pointer 
                    transition-all duration-200 shadow-lg text-white
                    ${isUpdatingProfile ? "animate-pulse pointer-events-none" : ""}
                  `}
                >
                  <Camera className="w-5 h-5" />
                  <input
                    type="file"
                    id="avatar-upload"
                    className="hidden"
                    accept="image/*"
                    onChange={handleImageUpload}
                    disabled={isUpdatingProfile}
                  />
                </label>
              </div>
            </div>
          </div>

          {/* Nội dung bên dưới */}
          <div className="pt-20 md:pt-6 md:pl-60 p-8">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div>
                <h1 className="text-3xl font-black text-rose-950 flex items-center gap-2">
                  {authUser?.fullName}
                  <BadgeCheck className="text-blue-500 fill-blue-50" size={24} />
                </h1>
                <p className="text-rose-400 font-medium">Thành viên ưu tú của Lovera</p>
              </div>
              <div className="flex gap-2">
                <span className="px-4 py-2 bg-green-50 text-green-600 rounded-xl text-sm font-bold border border-green-100 flex items-center gap-1">
                   <div className="size-2 bg-green-500 rounded-full animate-pulse" />
                   Đang hoạt động
                </span>
              </div>
            </div>

            <div className="grid md:grid-cols-2 gap-8 mt-12">
              {/* Cột trái: Thông tin cá nhân */}
              <div className="space-y-6">
                <h3 className="text-lg font-bold text-rose-900 border-b border-rose-100 pb-2">Thông tin cơ bản</h3>
                
                <div className="space-y-4">
                  <div className="group">
                    <label className="text-xs font-bold text-rose-300 uppercase tracking-widest flex items-center gap-2 mb-2">
                      <User size={14} /> Họ và tên
                    </label>
                    <div className="px-5 py-3.5 bg-rose-50/50 rounded-2xl border border-rose-100 text-rose-900 font-semibold group-focus-within:border-rose-300 transition-all">
                      {authUser?.fullName}
                    </div>
                  </div>

                  <div className="group">
                    <label className="text-xs font-bold text-rose-300 uppercase tracking-widest flex items-center gap-2 mb-2">
                      <Mail size={14} /> Địa chỉ Email
                    </label>
                    <div className="px-5 py-3.5 bg-rose-50/50 rounded-2xl border border-rose-100 text-rose-900 font-semibold italic">
                      {authUser?.email}
                    </div>
                  </div>
                </div>
              </div>

              {/* Cột phải: Trạng thái tài khoản */}
              <div className="space-y-6">
                <h3 className="text-lg font-bold text-rose-900 border-b border-rose-100 pb-2">Hệ thống</h3>
                
                <div className="bg-rose-50/30 rounded-3xl p-6 border border-rose-100 space-y-4">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-rose-400 flex items-center gap-2">
                      <Calendar size={16} /> Ngày tham gia
                    </span>
                    <span className="font-bold text-rose-900">{authUser.createdAt?.split("T")[0]}</span>
                  </div>
                  
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-rose-400 flex items-center gap-2">
                      <ShieldCheck size={16} /> Xác thực danh tính
                    </span>
                    <span className="text-blue-600 font-bold">Đã xác minh</span>
                  </div>

                  <div className="pt-4 mt-4 border-t border-rose-100">
                    <p className="text-[11px] text-rose-400 text-center leading-relaxed">
                      Thông tin của bạn được bảo mật theo tiêu chuẩn mã hóa SSL. 
                      Lovera cam kết không chia sẻ dữ liệu khi chưa có sự đồng ý của bạn.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Nút thao tác dưới cùng */}
            <div className="mt-12 flex justify-end gap-4">
               <button className="px-8 py-3 rounded-2xl font-bold text-rose-500 hover:bg-rose-50 transition-colors">
                  Đăng xuất
               </button>
               <button className="px-8 py-3 bg-gradient-to-r from-rose-500 to-rose-400 text-white rounded-2xl font-bold shadow-lg shadow-rose-200 hover:scale-105 transition-all">
                  Cập nhật hồ sơ
               </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;