const MessageSkeleton = () => {
  // Tạo 6 tin nhắn giả lập
  const skeletonMessages = Array(6).fill(0);

  return (
    <div className="space-y-8 h-full">
      {skeletonMessages.map((_, idx) => {
        const isMyMessageSkeleton = idx % 2 === 0; // Giả lập tin nhắn của tôi và người khác
        
        return (
          <div
            key={idx}
            className={`flex ${isMyMessageSkeleton ? "justify-end" : "justify-start"} gap-3 items-end relative overflow-hidden`}
          >
             {/* Hiệu ứng chạy sáng */}
             <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/80 to-transparent skew-x-12 animate-shine z-10"></div>
             
             {/* Avatar Skeleton */}
             {!isMyMessageSkeleton && (
               <div className="size-10 rounded-full bg-gray-100 z-0"></div>
             )}
             
             {/* Bong bóng tin nhắn Skeleton */}
             <div className={`
                p-4 rounded-[1.5rem] h-20 w-[60%]
                ${isMyMessageSkeleton 
                  ? "bg-rose-100 rounded-br-lg" 
                  : "bg-gray-100 rounded-bl-lg"
                }
             z-0`}>
                <div className="h-6 bg-white/50 rounded-md w-full mb-3"></div>
                <div className="h-4 bg-white/50 rounded-md w-3/4"></div>
             </div>
             
             {/* Avatar của tôi Skeleton */}
             {isMyMessageSkeleton && (
               <div className="size-10 rounded-full bg-rose-200 z-0"></div>
             )}
          </div>
        );
      })}
    </div>
  );
};

export default MessageSkeleton;