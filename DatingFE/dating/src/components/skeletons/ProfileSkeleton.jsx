const ProfileSkeleton = () => (
  <div className="min-h-screen bg-[#FFF5F7] pt-20 pb-12 px-4">
    <div className="max-w-6xl mx-auto animate-pulse">
      {/* Header Skeleton */}
      <div className="relative mb-8">
        <div className="bg-rose-50 h-60 md:h-80 rounded-[2.5rem] border border-rose-100" />
        <div className="absolute -bottom-6 left-10 size-32 rounded-[2rem] bg-white p-2 shadow-xl">
          <div className="w-full h-full rounded-[1.5rem] bg-rose-100" />
        </div>
      </div>
      
      <div className="grid grid-cols-12 gap-8 items-start mt-12">
        {/* Sidebar Skeleton */}
        <div className="col-span-12 md:col-span-4 flex flex-col gap-6">
          <div className="bg-white p-6 rounded-[2rem] border border-rose-50 shadow-sm h-40" />
          <div className="bg-white p-6 rounded-[2rem] border border-rose-50 shadow-sm h-60" />
        </div>
        
        {/* Timeline Area - Để trống hoặc chỉ để 1 cái khung CreatePost giả */}
        <div className="col-span-12 md:col-span-8 space-y-6">
          <div className="bg-white h-24 rounded-[2rem] border border-rose-50 shadow-sm" />
          {/* KHÔNG gọi PostSkeleton ở đây nữa */}
        </div>
      </div>
    </div>
  </div>
);
export default ProfileSkeleton;