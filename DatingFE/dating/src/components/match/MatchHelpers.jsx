// ============================================================
//  MatchHelpers.jsx  –  Named exports, KHÔNG có default export
//  Import: import { SkeletonCard, LikedRow } from "./MatchHelpers"
// ============================================================
import { Heart, MapPin, MessageCircle } from "lucide-react";

// ── Loading skeleton cho mỗi card ──
export const SkeletonCard = () => (
  <div
    className="rounded-3xl overflow-hidden animate-pulse bg-base-100 shadow-md"
    style={{ aspectRatio: "3/4" }}
  >
    <div className="w-full h-full bg-base-300" />
  </div>
);

// ── Hàng trong tab "Đã thích" ──
export const LikedRow = ({ user, onMessage }) => (
  <div className="flex items-center gap-3 p-3 bg-base-100 rounded-2xl border border-base-300 hover:border-pink-300 hover:shadow-md transition-all group">
    <div className="relative shrink-0">
      <img
        src={user.profilePic || "/avatar.png"}
        alt={user.fullName}
        className="w-12 h-12 rounded-full object-cover"
      />
      <div className="absolute -bottom-0.5 -right-0.5 w-4 h-4 bg-pink-500 rounded-full border-2 border-base-100 flex items-center justify-center">
        <Heart className="w-2 h-2 text-white fill-white" />
      </div>
    </div>

    <div className="flex-1 min-w-0">
      <p className="font-semibold text-sm truncate">{user.fullName}</p>
      <p className="text-xs text-base-content/45 flex items-center gap-1">
        <MapPin className="w-3 h-3 text-rose-400" />
        {user.location || "Việt Nam"}
      </p>
    </div>

    <button
      onClick={() => onMessage(user)}
      className="opacity-0 group-hover:opacity-100 btn btn-xs btn-primary rounded-xl gap-1 transition-all"
    >
      <MessageCircle className="w-3 h-3" /> Chat
    </button>
  </div>
);