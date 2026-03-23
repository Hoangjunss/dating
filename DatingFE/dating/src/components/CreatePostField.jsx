import { useState, useRef } from "react";
import { usePostStore } from "../store/usePostStore";
import { Image as ImageIcon, X, Send, Loader2 } from "lucide-react";

const CreatePostField = ({ authUser }) => {
  const [content, setContent] = useState("");
  const [image, setImage] = useState(null);
  const fileInputRef = useRef(null);
  const { createPost, isCreatingPost } = usePostStore();

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => setImage(reader.result);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim() && !image) return;

    await createPost({
      userId: authUser.userId || authUser._id,
      content,
      imageUrl: image, // Backend sẽ nhận base64 hoặc link tùy cấu hình
    });

    // Reset form sau khi đăng
    setContent("");
    setImage(null);
  };

  return (
    <div className="bg-white rounded-[2rem] p-6 shadow-lg shadow-rose-100 border border-rose-50 mb-6">
      <div className="flex items-center gap-4 mb-4">
        <div className="size-12 rounded-full overflow-hidden border border-rose-100">
          <img src={authUser?.profilePic || "/avatar.png"} className="w-full h-full object-cover" />
        </div>
        <textarea
          className="flex-1 bg-rose-50/50 rounded-2xl px-5 py-3 text-sm outline-none focus:ring-1 ring-rose-200 resize-none min-h-[50px]"
          placeholder={`${authUser?.fullName} ơi, hôm nay bạn thế nào?`}
          value={content}
          onChange={(e) => setContent(e.target.value)}
        />
      </div>

      {/* Hiển thị ảnh xem trước nếu có */}
      {image && (
        <div className="relative mb-4 w-full max-h-60 rounded-xl overflow-hidden border border-rose-100">
          <img src={image} className="w-full h-full object-cover" />
          <button 
            onClick={() => setImage(null)}
            className="absolute top-2 right-2 p-1 bg-rose-500 text-white rounded-full hover:bg-rose-600"
          >
            <X size={16} />
          </button>
        </div>
      )}

      <div className="flex justify-between items-center pt-2 border-t border-rose-50">
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          className="flex items-center gap-2 text-rose-400 hover:text-rose-500 transition px-2 font-medium text-sm"
        >
          <ImageIcon size={20} />
          <span>Ảnh/Video</span>
        </button>
        <input type="file" hidden ref={fileInputRef} onChange={handleImageChange} accept="image/*" />

        <button
          onClick={handleSubmit}
          disabled={isCreatingPost || (!content.trim() && !image)}
          className="bg-rose-500 text-white px-6 py-2 rounded-xl hover:bg-rose-600 disabled:opacity-50 transition flex items-center gap-2 text-sm font-bold"
        >
          {isCreatingPost ? <Loader2 className="animate-spin" size={18} /> : <Send size={18} />}
          Đăng bài
        </button>
      </div>
    </div>
  );
};

export default CreatePostField;