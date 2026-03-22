const ProfileSkeleton = () => (
  <div className="min-h-screen bg-[#FFF5F7] pt-20 pb-12 px-4">
    <div className="max-w-6xl mx-auto animate-pulse">
      {/* Header Skeleton */}
      <div className="bg-gray-200 h-60 md:h-80 rounded-[2.5rem] mb-8" />
      
      <div className="grid grid-cols-12 gap-8 items-start">
        {/* Sidebar Skeleton */}
        <div className="hidden md:flex md:col-span-4 flex-col gap-6">
          <div className="bg-gray-200 h-64 rounded-[2rem]" />
          <div className="bg-gray-200 h-48 rounded-[2rem]" />
        </div>
        
        {/* Timeline Skeleton */}
        <div className="col-span-12 md:col-span-8 space-y-6">
          <div className="bg-gray-200 h-24 rounded-[2rem]" />
          <div className="bg-gray-200 h-[400px] rounded-[2.5rem]" />
        </div>
      </div>
    </div>
  </div>
);

export default ProfileSkeleton;