import { Sparkles, Image as ImageIcon } from "lucide-react";

const ProfileSidebar = ({ bio, interests, postedImages }) => (
  <div className="hidden md:flex md:col-span-4 flex-col gap-6 sticky top-24">
    <div className="bg-white rounded-[2rem] p-8 shadow-lg shadow-rose-100 border border-rose-50">
      <h3 className="text-lg font-bold text-rose-900 border-b border-rose-100 pb-2 flex items-center gap-2">
        <Sparkles size={18}/> Giới thiệu
      </h3>
      <div className="mt-4">
        <p className="text-rose-800 text-sm font-medium leading-relaxed italic">"{bio}"</p>
      </div>
      <div className="mt-6 pt-6 border-t border-rose-50">
        <div className="flex flex-wrap gap-2">
          {interests.map((interest) => (
  <span 
    key={interest.id} 
    className="px-3 py-1 bg-rose-100 text-rose-600 rounded-full text-sm font-medium"
  >
    {interest.name} {/* Chỗ này quan trọng: Phải lấy .name ra để hiển thị */}
  </span>
))}
        </div>
      </div>
    </div>

    <div className="bg-white rounded-[2rem] p-6 shadow-lg shadow-rose-100 border border-rose-50">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-lg font-bold text-rose-900 flex items-center gap-2">
          <ImageIcon size={18} className="text-rose-500" /> Ảnh đã đăng
        </h3>
        <span className="text-xs text-rose-300 font-bold">{postedImages.length} ảnh</span>
      </div>
      <div className="grid grid-cols-2 gap-2">
        {postedImages.map((img, index) => (
          <div key={index} className="aspect-square rounded-xl overflow-hidden border border-rose-50">
            <img src={img} className="w-full h-full object-cover hover:scale-110 transition duration-500" alt="posted" />
          </div>
        ))}
      </div>
      <button className="w-full mt-4 py-2 bg-rose-50 text-rose-500 text-xs font-bold rounded-xl hover:bg-rose-100 transition">
        Xem tất cả
      </button>
    </div>
  </div>
);

export default ProfileSidebar;