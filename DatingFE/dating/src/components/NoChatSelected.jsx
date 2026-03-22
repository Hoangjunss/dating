import { Zap, Heart, Sparkles } from "lucide-react";

const NoChatSelected = () => {
  return (
    <div className="w-full h-full flex flex-col items-center justify-center p-12 bg-rose-50/20">
      <div className="max-w-md text-center flex flex-col items-center gap-10">
        
        {/* IconZap mờ ảo, tông màu hồng */}
        <div className="relative">
          <div className="w-28 h-28 bg-rose-100 rounded-[2.5rem] flex items-center justify-center shadow-inner relative z-10 border border-rose-200 shadow-rose-200/50">
            <Zap className="size-16 text-rose-500 fill-rose-100" />
          </div>
          {/* Họa tiết nền mờ */}
          <Heart className="absolute -top-10 -right-10 size-16 text-rose-200 opacity-60 fill-rose-200 blur-sm rotate-12 z-0" />
          <Sparkles className="absolute -bottom-8 -left-8 size-14 text-orange-200 opacity-70 fill-orange-200 blur-sm z-0" />
        </div>

        {/* Chữ chào mừng - Tông màu hồng đậm */}
        <div className="space-y-4">
          <h2 className="text-4xl font-black text-rose-950 leading-tight">Mở lời yêu thương,<br />kết nối <span className="text-transparent bg-clip-text bg-gradient-to-r from-rose-500 to-orange-400">tâm hồn</span></h2>
          <p className="text-lg font-medium text-rose-800 leading-relaxed max-w-sm mx-auto">
             Chào bạn đến với Lovera Chats! <br /> Chọn một "nửa kia" từ Sidebar để bắt đầu câu chuyện tình yêu của bạn.
          </p>
        </div>
      </div>
    </div>
  );
};

export default NoChatSelected;