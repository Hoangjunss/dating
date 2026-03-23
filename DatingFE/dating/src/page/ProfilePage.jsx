import { useState, useEffect } from "react";
import { useAuthStore } from "../store/useAuthStore";
import { useProfileStore } from "../store/useProfileStore";
import ProfileHeader from "../components/ProfileHeader";
import ProfileSidebar from "../components/ProfileSidebar";
import ProfileSkeleton from "../components/skeletons/ProfileSkeleton.jsx";

// Import 2 thành phần mới để xử lý Post thật
import CreatePostField from "../components/CreatePostField"; 
import PostContainer from "../components/PostContainer"; 

const ProfilePage = () => {
  const { authUser, isUpdatingProfile, updateProfile } = useAuthStore();
  const { userProfile, getMyProfile, isProfileLoading } = useProfileStore();
  const [selectedImg, setSelectedImg] = useState(null);

  // --- GIỮ LẠI DỮ LIỆU MẪU THEO Ý DUNG ---
  const [defaultPostedImages] = useState([
    "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=400",
    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?q=80&w=400",
    "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?q=80&w=400",
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=400",
  ]);

  useEffect(() => {
    // Lấy userId để fetch profile
    const userId = authUser?.userId || authUser?._id;
    if (userId) {
      getMyProfile(userId);
    }
  }, [authUser, getMyProfile]);

  const handleImageUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = async () => {
      const base64Image = reader.result;
      setSelectedImg(base64Image);
      await updateProfile({ profilePic: base64Image });
    };
  };

  // Hiển thị loading khi chưa có dữ liệu profile
  if (isProfileLoading && !userProfile) return <ProfileSkeleton />;

  const currentUserId = authUser?.userId || authUser?._id;

  return (
    <div className="min-h-screen bg-[#FFF5F7] pt-20 pb-12 px-4 selection:bg-rose-200">
      <div className="max-w-6xl mx-auto">
        <ProfileHeader 
          authUser={authUser} 
          selectedImg={selectedImg || userProfile?.image} 
          handleImageUpload={handleImageUpload} 
          isUpdatingProfile={isUpdatingProfile} 
        />

        <div className="grid grid-cols-12 gap-8 items-start">
          <ProfileSidebar 
            bio={userProfile?.bio || "Chưa có tiểu sử"} 
            interests={userProfile?.interestResponses || []} 
            // Fallback: nếu gallery trống thì dùng mảng default của Dung
            postedImages={userProfile?.gallery?.length > 0 ? userProfile.gallery : defaultPostedImages} 
          />

          <div className="col-span-12 md:col-span-8 space-y-6">
            {/* Thanh đăng bài: Gọi API tạo bài mới */}
            <CreatePostField authUser={authUser} userProfile={userProfile} />

            {/* Container Post: Gọi API lấy danh sách bài đăng theo userId */}
            <PostContainer userId={currentUserId} authUser={authUser} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;