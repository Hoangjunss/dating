import { useEffect } from "react";
import { usePostStore } from "../store/usePostStore"; // Giả sử Dung có store này
import PostCard from "./PostCard";
import PostSkeleton from "./skeletons/PostSkeleton";

const PostContainer = ({ userId, authUser }) => {
  const { posts, getPostsByUserId, isPostsLoading } = usePostStore();

  useEffect(() => {
    if (userId) {
      getPostsByUserId(userId); // Gọi API riêng biệt: GET /api/posts/user/{userId}
    }
  }, [userId, getPostsByUserId]);

  if (isPostsLoading) return <PostSkeleton />;

  return (
    <div className="space-y-6">
      {posts.length > 0 ? (
        posts.map((post) => (
          <PostCard key={post.id} post={post} authUser={authUser} />
        ))
      ) : (
        <div className="bg-white rounded-[2rem] p-10 text-center text-rose-300 border border-dashed border-rose-200">
          Chưa có bài đăng nào ở đây cả... ✨
        </div>
      )}
    </div>
  );
};

export default PostContainer;