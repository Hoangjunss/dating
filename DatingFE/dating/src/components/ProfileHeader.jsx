import { Camera, BadgeCheck, MapPin, Flame } from "lucide-react";

const ProfileHeader = ({ authUser, selectedImg, handleImageUpload, isUpdatingProfile }) => (
  <div className="bg-white rounded-[2.5rem] shadow-xl shadow-rose-100/50 overflow-hidden border border-rose-50 mb-8 relative">
    <div className="h-60 md:h-80 bg-gradient-to-r from-rose-400 to-orange-300 relative">
      <img src="https://images.unsplash.com/photo-1516589174184-c6852657d48d?q=80&w=1600" className="w-full h-full object-cover opacity-80" alt="Cover" />
    </div>

    <div className="pt-24 md:pt-6 md:pl-60 p-8 relative">
      <div className="absolute -top-16 left-1/2 -translate-x-1/2 md:left-12 md:translate-x-0 group">
        <img
          src={selectedImg || authUser.profilePic || "/avatar.png"}
          alt="Profile"
          className="size-32 md:size-40 rounded-[2.5rem] object-cover border-8 border-white shadow-xl"
        />
        <label htmlFor="avatar-upload" className="absolute bottom-2 -right-2 bg-rose-500 p-3 rounded-2xl cursor-pointer text-white shadow-lg">
          <Camera className="w-5 h-5" />
          <input type="file" id="avatar-upload" className="hidden" accept="image/*" onChange={handleImageUpload} />
        </label>
      </div>

      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-4xl font-black text-rose-950 flex items-center gap-2.5">
            {authUser?.fullName}
            <BadgeCheck className="text-blue-500 fill-white rounded-full p-0.5" size={26} />
          </h1>
          <p className="text-rose-400 font-medium flex items-center gap-2 mt-1">
            <MapPin size={15}/> Quận 1, HCM • <Flame size={15} className="text-orange-400 fill-orange-400"/> Hot Member
          </p>
        </div>
        <button className="px-7 py-3 bg-gradient-to-r from-rose-500 to-rose-400 text-white rounded-xl font-bold shadow-lg shadow-rose-200 hover:scale-105 transition-all">
          Cập nhật hồ sơ
        </button>
      </div>
    </div>
  </div>
);

export default ProfileHeader;