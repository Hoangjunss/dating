const PostSkeleton = () => {
  // Tạo mảng 3 phần tử để hiện 3 khung bài viết giả lúc đang load
  const skeletonItems = Array.from({ length: 3 });

  return (
    <div className="space-y-6">
      {skeletonItems.map((_, idx) => (
        <div 
          key={idx} 
          className="bg-white rounded-[2rem] p-6 shadow-lg shadow-rose-100 border border-rose-50 animate-pulse"
        >
          {/* Header Skeleton: Avatar + Name */}
          <div className="flex items-center gap-3 mb-4">
            <div className="size-10 rounded-full bg-rose-100" />
            <div className="space-y-2">
              <div className="h-4 w-32 bg-rose-100 rounded-md" />
              <div className="h-3 w-20 bg-rose-50 rounded-md" />
            </div>
          </div>

          {/* Content Skeleton: Dòng chữ giả */}
          <div className="space-y-2 mb-4">
            <div className="h-4 w-full bg-rose-50 rounded-md" />
            <div className="h-4 w-3/4 bg-rose-50 rounded-md" />
          </div>

          {/* Image Skeleton: Khung ảnh to */}
          <div className="aspect-video w-full bg-rose-100/50 rounded-2xl mb-4" />

          {/* Footer Skeleton: Like/Comment buttons */}
          <div className="flex gap-4 pt-2 border-t border-rose-50">
            <div className="h-8 w-16 bg-rose-50 rounded-xl" />
            <div className="h-8 w-16 bg-rose-50 rounded-xl" />
          </div>
        </div>
      ))}
    </div>
  );
};

export default PostSkeleton;