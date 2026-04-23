import { Heart, MessageCircle, Check } from "lucide-react";

// Avatar mặc định khi user chưa có ảnh
const DefaultMatchAvatar = () => (
  <div className="absolute inset-0 flex items-center justify-center" style={{ background: "#1e293b" }}>
    <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg" className="w-full h-full">
      <rect width="200" height="200" fill="#1e293b" />
      <circle cx="100" cy="78" r="38" fill="#334155" />
      <ellipse cx="100" cy="175" rx="58" ry="42" fill="#334155" />
    </svg>
  </div>
);

const MatchCard = ({ user, onMessage }) => {
  const displayName = user.displayName || "Ẩn danh";
  const photoUrl    = user.photoUrls?.[0] ?? null;

  return (
    <div
      className="relative rounded-3xl overflow-hidden shadow-md cursor-pointer hover:shadow-2xl hover:-translate-y-1 transition-all duration-300 ring-2 ring-pink-400 ring-offset-2 ring-offset-base-200"
      style={{ aspectRatio: "3/4" }}
      onClick={() => onMessage(user)}
    >
      {photoUrl ? (
        <img
          src={photoUrl}
          alt={displayName}
          className="absolute inset-0 w-full h-full object-cover"
          onError={(e) => { e.currentTarget.style.display = "none"; }}
        />
      ) : (
        <DefaultMatchAvatar />
      )}

      <div
        className="absolute inset-0"
        style={{ background: "linear-gradient(to top, rgba(0,0,0,0.75) 40%, transparent 80%)" }}
      />

      {/* Badge match */}
      <div className="absolute top-3 right-3 bg-gradient-to-br from-pink-500 to-rose-500 rounded-full p-2 shadow-lg">
        <Heart className="w-4 h-4 text-white fill-white" />
      </div>

      {/* Info */}
      <div className="absolute bottom-0 left-0 right-0 p-4">
        <div className="flex items-center gap-1.5 mb-1">
          <Check className="w-3.5 h-3.5 text-emerald-400" />
          <span className="text-emerald-400 text-xs font-semibold">Đã match!</span>
        </div>
        <p className="text-white font-bold text-base">
          {displayName}{user.age ? `, ${user.age}` : ""}
        </p>
        <button className="mt-2 w-full flex items-center justify-center gap-1.5 py-2 rounded-2xl
          bg-white/20 hover:bg-primary/80 backdrop-blur-sm text-white text-sm font-semibold
          border border-white/25 transition-all">
          <MessageCircle className="w-4 h-4" /> Nhắn tin
        </button>
      </div>
    </div>
  );
};

export default MatchCard;