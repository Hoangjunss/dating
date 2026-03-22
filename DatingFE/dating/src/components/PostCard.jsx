import { Heart, MessageCircle, MoreHorizontal, BadgeCheck } from "lucide-react";

const PostCard = ({ post, authUser }) => (
  <div className="bg-white rounded-[2.5rem] p-8 shadow-lg shadow-rose-100 border border-rose-50 hover:shadow-2xl transition-all duration-500">
    <div className="flex justify-between items-start mb-6">
      <div className="flex gap-4">
        <div className="size-12 rounded-full border-2 border-rose-100">
          <img src={authUser.profilePic || "/avatar.png"} className="w-full h-full object-cover rounded-full" />
        </div>
        <div>
          <h4 className="font-bold text-gray-900 flex items-center gap-1.5">{authUser?.fullName} <BadgeCheck size={16} className="text-blue-500 fill-blue-50"/></h4>
          <p className="text-rose-300 text-[11px] font-bold uppercase tracking-tighter">{post.createdAt}</p>
        </div>
      </div>
      <MoreHorizontal size={20} className="text-gray-300 cursor-pointer" />
    </div>

    <p className="text-gray-800 font-medium leading-relaxed mb-6">{post.content}</p>
    
    {post.image && (
      <div className="rounded-[1.5rem] overflow-hidden border border-rose-50 mb-6 max-h-[400px]">
        <img src={post.image} className="w-full h-full object-cover" alt="post-img" />
      </div>
    )}

    <div className="flex items-center gap-6 pt-6 border-t border-rose-50 text-gray-400">
      <div className="flex items-center gap-2 cursor-pointer hover:text-rose-500 transition font-bold text-xs uppercase">
        <Heart size={18} /> {post.likes}
      </div>
      <div className="flex items-center gap-2 cursor-pointer hover:text-rose-500 transition font-bold text-xs uppercase">
        <MessageCircle size={18} /> {post.comments}
      </div>
    </div>
  </div>
);

export default PostCard;