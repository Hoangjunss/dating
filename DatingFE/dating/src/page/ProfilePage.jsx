import { useState, useEffect } from "react";
import { useAuthStore } from "../store/useAuthStore";
import { useProfileStore } from "../store/useProfileStore"; // Store mới của Dung
import { Image as ImageIcon } from "lucide-react";
import ProfileHeader from "../components/ProfileHeader";
import ProfileSidebar from "../components/ProfileSidebar";
import PostCard from "../components/PostCard";
import ProfileSkeleton from "../components/skeletons/ProfileSkeleton.jsx";

const ProfilePage = () => {
  const { authUser, isUpdatingProfile, updateProfile } = useAuthStore();
  const { userProfile, getMyProfile, isProfileLoading } = useProfileStore();
  const [selectedImg, setSelectedImg] = useState(null);

  // --- 3 ĐOẠN DỮ LIỆU MẪU DUNG MUỐN GIỮ LẠI ---
  const [defaultBio] = useState("Đang tìm người cùng tần số, thích cafe cuối tuần và xem phim kinh dị...");
  const [defaultInterests] = useState(["Du lịch", "Cafe", "Xem phim", "Sách", "Photography"]);
  const [defaultPostedImages] = useState([
    "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=400",
    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?q=80&w=400",
    "https://images.unsplash.com/photo-1529626455594-4ff0802cfb7e?q=80&w=400",
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=400",
  ]);

  useEffect(() => {
    // Lấy userId từ AuthStore để gọi API Profile
    const userId = authUser?.userId || authUser?._id;
    if (userId) {
      getMyProfile(userId);
    }
  }, [authUser, getMyProfile]);

  const [timelinePosts] = useState([
    { id: 1, createdAt: "2 giờ trước", content: "Một buổi chiều bình yên tại Đà Lạt...", image: "https://images.unsplash.com/photo-1517841905240-472988babdf9?q=80&w=600", likes: 152, comments: 24 },
  ]);

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

  if (isProfileLoading && !userProfile) return <ProfileSkeleton />;

  return (
    <div className="min-h-screen bg-[#FFF5F7] pt-20 pb-12 px-4 selection:bg-rose-200">
      <div className="max-w-6xl mx-auto">
        <ProfileHeader 
          authUser={authUser} 
          selectedImg={selectedImg} 
          handleImageUpload={handleImageUpload} 
          isUpdatingProfile={isUpdatingProfile} 
        />

        <div className="grid grid-cols-12 gap-8 items-start">
          <ProfileSidebar 
            /* Ưu tiên lấy từ userProfile (BE), 
               nếu không có thì lấy từ biến default (của Dung) 
            */
            bio={userProfile?.bio || defaultBio} 
            interests={userProfile?.interests?.length > 0 ? userProfile.interests : defaultInterests} 
            postedImages={userProfile?.gallery?.length > 0 ? userProfile.gallery : defaultPostedImages} 
          />

          <div className="col-span-12 md:col-span-8 space-y-6">
            <div className="bg-white rounded-[2rem] p-6 shadow-lg shadow-rose-100 border border-rose-50 flex items-center gap-4">
               <div className="size-12 rounded-full overflow-hidden border border-rose-100">
                  <img src={authUser?.profilePic || "/avatar.png"} className="w-full h-full object-cover" />
               </div>
               <div className="flex-1 bg-rose-50/50 rounded-2xl px-5 py-3 text-rose-400 font-medium cursor-pointer hover:bg-rose-100 transition text-sm">
                  {authUser?.fullName} ơi, hôm nay bạn thế nào?
               </div>
               <button className="text-rose-400 hover:text-rose-500 transition px-2"><ImageIcon size={22} /></button>
            </div>

            {timelinePosts.map(post => (
              <PostCard key={post.id} post={post} authUser={authUser} />
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;