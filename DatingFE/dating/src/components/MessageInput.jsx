import { useRef, useState } from "react";
import { useChatStore } from "../store/useChatStore";
import { Image, Send, X, Smile } from "lucide-react";
import toast from "react-hot-toast";

const MessageInput = () => {
  const [text, setText] = useState("");
  const [imagePreview, setImagePreview] = useState(null);
  const fileInputRef = useRef(null);
  const { sendMessage } = useChatStore();

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file.type.startsWith("image/")) {
      toast.error("Vui lòng chọn một file hình ảnh");
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      setImagePreview(reader.result);
    };
    reader.readAsDataURL(file);
  };

  const removeImage = () => {
    setImagePreview(null);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!text.trim() && !imagePreview) return;

    try {
      await sendMessage({
        content: text.trim(),
        image: imagePreview,
      });

      // Xóa form sau khi gửi
      setText("");
      setImagePreview(null);
      if (fileInputRef.current) fileInputRef.current.value = "";
    } catch (error) {
      console.error("Lỗi gửi tin nhắn:", error);
    }
  };

  return (
    <div className="p-5 border-t border-rose-100 bg-white">
      {/* Hiển thị xem trước hình ảnh - Bento style */}
      {imagePreview && (
        <div className="mb-4 flex items-center gap-3 bg-rose-50/50 p-3 rounded-2xl border border-rose-100/50 relative">
          <img
            src={imagePreview}
            alt="Preview"
            className="size-20 md:size-24 object-cover rounded-xl border-4 border-white shadow-md"
          />
          <button
            onClick={removeImage}
            className="absolute top-2 right-2 p-1.5 bg-rose-500 text-white rounded-full hover:bg-rose-600 transition-colors shadow-lg active:scale-95"
            type="button"
          >
            <X className="size-4" />
          </button>
          <p className="text-xs text-rose-400 font-bold uppercase tracking-widest">Hình ảnh đính kèm</p>
        </div>
      )}

      {/* Form nhập liệu */}
      <form onSubmit={handleSendMessage} className="flex items-center gap-3">
        {/* Nút đính kèm ảnh & Nút Emoji (giả lập) */}
        <div className="flex gap-2.5">
            <button
              type="button"
              className={`p-3.5 bg-rose-50 rounded-2xl border border-rose-100/50 hover:bg-rose-100 transition-colors active:scale-95 ${imagePreview ? "text-rose-500" : "text-rose-400"}`}
              onClick={() => fileInputRef.current?.click()}
            >
              <Image size={22} />
            </button>
            <button
                type="button"
                className="p-3.5 bg-rose-50 rounded-2xl border border-rose-100/50 text-rose-400 hover:bg-rose-100 transition-colors active:scale-95"
            >
                <Smile size={22} />
            </button>
        </div>
        
        {/* Input ẩn để chọn file */}
        <input
          type="file"
          accept="image/*"
          className="hidden"
          ref={fileInputRef}
          onChange={handleImageChange}
        />

        {/* Ô nhập văn bản - Thiết kế tròn trịa, hiện đại */}
        <div className="flex-1 relative group">
          <input
            type="text"
            className="w-full bg-rose-50 p-4 pr-14 rounded-2xl border border-rose-100/50 outline-none text-rose-950 placeholder:text-rose-300 focus:border-rose-300 focus:ring-2 focus:ring-rose-100 transition text-sm md:text-base font-medium"
            placeholder="Viết lời thương gửi người ấy..."
            value={text}
            onChange={(e) => setText(e.target.value)}
          />
        </div>

        {/* Nút gửi tin nhắn - Nổi bật, gradient hồng */}
        <button
          type="submit"
          className="p-4 bg-gradient-to-r from-rose-500 to-rose-400 text-white rounded-2xl hover:shadow-[0_0_20px_rgba(244,63,94,0.4)] hover:scale-105 transition-all transform active:scale-95 disabled:opacity-50 shadow-lg shadow-rose-200"
          disabled={!text.trim() && !imagePreview}
        >
          <Send size={22} />
        </button>
      </form>
    </div>
  );
};
export default MessageInput;