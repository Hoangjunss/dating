import { useState, useEffect } from "react";
import { useAuthStore } from "../store/useAuthStore";
import { useChatStore } from "../store/useChatStore";
import { useNavigate } from "react-router-dom";
import {
  Heart, X, MessageCircle, Sparkles,
  Users, Star, Zap, RefreshCw, Filter,
  MapPin, Clock, Check,
} from "lucide-react";
import toast from "react-hot-toast";

// ╔══════════════════════════════════════════════════════════════╗
//  MOCK DATA – Xoá import này khi có API thật
// ╚══════════════════════════════════════════════════════════════╝
import { MOCK_USERS, MOCK_ONLINE_USERS, MOCK_CURRENT_USER } from "../data/mockData";

// ── TODO: Thay bằng GET /api/match/suggestions ─────────────────
const fetchSuggestedUsers = async (currentUserId) => {
  await new Promise((r) => setTimeout(r, 400));
  return MOCK_USERS.filter((u) => u.userId !== currentUserId);
};
// ── TODO: Thay bằng POST /api/match/like/:id ───────────────────
const postLike = async (_id) => {
  await new Promise((r) => setTimeout(r, 150));
  return { isMatch: Math.random() > 0.6 };
};
// ── TODO: Thay bằng POST /api/match/skip/:id ───────────────────
const postSkip = async (_id) => { /* no-op */ };
// ╚══════════════════════════════════════════════════════════════╝

// ── Skeleton card ───────────────────────────────────────────────
const SkeletonCard = () => (
  <div className="bg-base-100 rounded-2xl overflow-hidden border border-base-300 animate-pulse">
    <div className="h-52 bg-base-300" />
    <div className="p-4 space-y-2">
      <div className="h-4 bg-base-300 rounded w-3/4" />
      <div className="h-3 bg-base-300 rounded w-1/2" />
      <div className="flex gap-2 pt-2">
        {[1,2].map(i => <div key={i} className="h-5 w-14 bg-base-300 rounded-full" />)}
      </div>
    </div>
  </div>
);

// ── User card in grid ───────────────────────────────────────────
const UserCard = ({ user, onLike, onSkip, onMessage, isOnline, isLiked, isSkipped }) => {
  const [imgLoaded, setImgLoaded] = useState(false);
  const [localLiked,  setLocalLiked]  = useState(false);
  const [localSkipped, setLocalSkipped] = useState(false);

  const handleLike = async () => {
    if (localLiked || localSkipped) return;
    setLocalLiked(true);
    await onLike(user);
  };
  const handleSkip = async () => {
    if (localLiked || localSkipped) return;
    setLocalSkipped(true);
    await onSkip(user);
  };

  const interests = user.interests || ["Chat", "Bạn bè"];

  return (
    <div className={`relative bg-base-100 rounded-2xl overflow-hidden border transition-all duration-300 shadow-sm hover:shadow-lg hover:-translate-y-0.5
      ${localLiked  ? "border-pink-400 ring-2 ring-pink-400/30" :
        localSkipped ? "border-base-300 opacity-50" :
                       "border-base-300 hover:border-primary/40"}`}>

      {/* Image */}
      <div className="relative h-52 bg-base-200 overflow-hidden">
        {!imgLoaded && (
          <div className="absolute inset-0 flex items-center justify-center bg-base-300">
            <Users className="w-10 h-10 text-base-content/20" />
          </div>
        )}
        <img
          src={user.profilePic || "/avatar.png"}
          alt={user.fullName}
          className={`w-full h-full object-cover transition-opacity duration-300 ${imgLoaded ? "opacity-100" : "opacity-0"}`}
          onLoad={() => setImgLoaded(true)}
        />
        <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent" />

        {/* Online dot */}
        {isOnline && (
          <div className="absolute top-3 right-3 flex items-center gap-1.5 bg-emerald-500/90 backdrop-blur-sm text-white text-xs font-semibold px-2.5 py-1 rounded-full shadow">
            <span className="w-1.5 h-1.5 bg-white rounded-full animate-pulse" />
            Online
          </div>
        )}

        {/* Liked overlay */}
        {localLiked && (
          <div className="absolute inset-0 bg-pink-500/20 flex items-center justify-center">
            <Heart className="w-14 h-14 text-pink-400 fill-pink-400 drop-shadow-lg" />
          </div>
        )}
        {localSkipped && (
          <div className="absolute inset-0 bg-base-300/40 flex items-center justify-center">
            <X className="w-14 h-14 text-base-content/40 drop-shadow" />
          </div>
        )}

        {/* Name on image */}
        <div className="absolute bottom-3 left-3 right-3 text-white">
          <p className="font-bold text-base leading-tight drop-shadow">{user.fullName}</p>
          {user.location && (
            <p className="text-white/75 text-xs flex items-center gap-1 mt-0.5">
              <MapPin className="w-3 h-3 shrink-0" />{user.location}
            </p>
          )}
        </div>
      </div>

      {/* Info */}
      <div className="p-3">
        {user.bio && (
          <p className="text-xs text-base-content/65 mb-2 line-clamp-2">{user.bio}</p>
        )}
        <div className="flex items-center gap-1.5 text-base-content/40 mb-2">
          <Clock className="w-3 h-3 shrink-0" />
          <span className="text-xs">
            {user.createdAt ? new Date(user.createdAt).toLocaleDateString("vi-VN") : "Gần đây"}
          </span>
        </div>
        <div className="flex flex-wrap gap-1 mb-3">
          {interests.slice(0, 3).map((tag) => (
            <span key={tag} className="badge badge-ghost badge-xs">#{tag}</span>
          ))}
        </div>

        {/* Actions */}
        <div className="flex gap-2">
          <button
            onClick={handleSkip}
            disabled={localLiked || localSkipped}
            className="flex-1 btn btn-xs btn-ghost border border-base-300 hover:border-red-400 hover:text-red-500 gap-1 rounded-xl disabled:opacity-30"
          >
            <X className="w-3 h-3" /> Bỏ qua
          </button>
          <button
            onClick={() => onMessage(user)}
            className="btn btn-xs btn-ghost border border-base-300 hover:border-primary hover:text-primary rounded-xl px-2"
          >
            <MessageCircle className="w-3.5 h-3.5" />
          </button>
          <button
            onClick={handleLike}
            disabled={localLiked || localSkipped}
            className={`flex-1 btn btn-xs gap-1 rounded-xl disabled:opacity-30
              ${localLiked
                ? "btn-primary text-white"
                : "btn-ghost border border-pink-300 text-pink-500 hover:bg-pink-50 hover:border-pink-500"}`}
          >
            <Heart className={`w-3 h-3 ${localLiked ? "fill-white" : ""}`} />
            {localLiked ? "Đã thích" : "Thích"}
          </button>
        </div>
      </div>
    </div>
  );
};

// ── Match result card ───────────────────────────────────────────
const MatchCard = ({ user, onMessage }) => (
  <div className="bg-base-100 rounded-2xl overflow-hidden border border-pink-300 shadow hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200">
    <div className="relative h-44 bg-base-200">
      <img src={user.profilePic || "/avatar.png"} alt={user.fullName} className="w-full h-full object-cover" />
      <div className="absolute inset-0 bg-gradient-to-t from-black/55 to-transparent" />
      <div className="absolute top-2 right-2 bg-gradient-to-r from-pink-500 to-rose-500 rounded-full p-1.5 shadow">
        <Heart className="w-3 h-3 text-white fill-white" />
      </div>
      <p className="absolute bottom-2 left-3 right-3 text-white font-bold text-sm truncate">{user.fullName}</p>
    </div>
    <div className="p-3">
      <div className="flex items-center gap-1 mb-2">
        <Check className="w-3 h-3 text-emerald-500" />
        <span className="text-xs text-emerald-600 font-semibold">Đã match!</span>
      </div>
      <button onClick={() => onMessage(user)} className="btn btn-xs btn-primary w-full rounded-xl gap-1">
        <MessageCircle className="w-3 h-3" /> Nhắn tin
      </button>
    </div>
  </div>
);

// ── Liked user row ──────────────────────────────────────────────
const LikedRow = ({ user, onMessage }) => (
  <div className="flex items-center gap-3 p-3 bg-base-100 rounded-xl border border-base-300 hover:border-primary/40 hover:shadow transition-all group">
    <img src={user.profilePic || "/avatar.png"} alt={user.fullName} className="w-10 h-10 rounded-full object-cover shrink-0" />
    <div className="flex-1 min-w-0">
      <p className="font-semibold text-sm truncate">{user.fullName}</p>
      <p className="text-xs text-base-content/45">{user.location || "Việt Nam"}</p>
    </div>
    <button onClick={() => onMessage(user)} className="opacity-0 group-hover:opacity-100 btn btn-xs btn-primary rounded-xl gap-1 transition-opacity">
      <MessageCircle className="w-3 h-3" /> Chat
    </button>
  </div>
);


// ══════════════════════════════════════════════════════════════
//  MAIN PAGE
// ══════════════════════════════════════════════════════════════
const MatchPage = () => {
  // ── TODO: Bỏ fallback MOCK_* khi store đã có data thật ──────
  const { authUser: storeAuth, onlineUsers: storeOnline } = useAuthStore();
  const authUser    = storeAuth   || MOCK_CURRENT_USER;
  const onlineUsers = storeOnline?.length ? storeOnline : MOCK_ONLINE_USERS;
  // ────────────────────────────────────────────────────────────

  const { setSelectedUser } = useChatStore();
  const navigate = useNavigate();

  const [allUsers,   setAllUsers]   = useState([]);
  const [isLoading,  setIsLoading]  = useState(true);
  const [likedUsers, setLikedUsers] = useState([]); // { userId }
  const [skippedIds, setSkippedIds] = useState(new Set());
  const [matches,    setMatches]    = useState([]);
  const [tab,        setTab]        = useState("discover");
  const [showFilter, setShowFilter] = useState(false);
  const [onlineOnly, setOnlineOnly] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    const load = async () => {
      setIsLoading(true);
      try {
        const pool = await fetchSuggestedUsers(authUser?.userId);
        setAllUsers(pool);
      } catch {
        toast.error("Không thể tải danh sách người dùng");
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, [authUser?.userId, refreshKey]);

  const visibleUsers = allUsers.filter((u) => {
    if (onlineOnly && !onlineUsers.includes(u._id)) return false;
    return true;
  });

  const handleLike = async (user) => {
    if (likedUsers.find((u) => u.userId === user.userId)) return;
    setLikedUsers((prev) => [...prev, user]);
    const { isMatch } = await postLike(user.userId);
    if (isMatch) {
      setMatches((prev) => [...prev, user]);
      toast.custom((t) => (
        <div className={`${t.visible ? "opacity-100" : "opacity-0"} transition-opacity max-w-xs bg-gradient-to-r from-pink-500 to-rose-500 text-white px-5 py-3 rounded-2xl shadow-2xl flex items-center gap-3`}>
          <Heart className="w-6 h-6 fill-white shrink-0" />
          <div>
            <p className="font-bold text-sm">It&apos;s a Match! 🎉</p>
            <p className="text-xs opacity-90">{user.fullName} cũng thích bạn!</p>
          </div>
        </div>
      ));
    }
  };

  const handleSkip = async (user) => {
    setSkippedIds((prev) => new Set([...prev, user.userId]));
    await postSkip(user.userId);
  };

  const handleMessage = (user) => { setSelectedUser(user); navigate("/"); };
  const handleRefresh = () => { setRefreshKey((k) => k + 1); setLikedUsers([]); setSkippedIds(new Set()); setMatches([]); toast.success("Đã làm mới!"); };

  const tabs = [
    { key: "discover", label: "Khám phá", icon: <Zap   className="w-4 h-4" /> },
    { key: "liked",    label: "Đã thích", icon: <Heart className="w-4 h-4" />, badge: likedUsers.length },
    { key: "matches",  label: "Matches",  icon: <Star  className="w-4 h-4" />, badge: matches.length   },
  ];

  const statsData = [
    { label: "Đề xuất",  value: visibleUsers.length,  icon: <Users  className="w-4 h-4" />, color: "text-primary"   },
    { label: "Đã thích", value: likedUsers.length,    icon: <Heart  className="w-4 h-4" />, color: "text-pink-500"  },
    { label: "Matched",  value: matches.length,       icon: <Star   className="w-4 h-4" />, color: "text-amber-500" },
  ];

  return (
    <div className="min-h-screen bg-base-200">

      {/* ── Sticky subheader ── */}
      <div className="sticky top-16 z-30 bg-base-200/90 backdrop-blur-md border-b border-base-300 px-4 py-2.5">
        <div className="max-w-6xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-pink-500 to-rose-500 flex items-center justify-center shadow">
              <Sparkles className="w-3.5 h-3.5 text-white" />
            </div>
            <h1 className="text-base font-bold">Khám Phá</h1>
          </div>
          <div className="flex items-center gap-1.5">
            <button onClick={handleRefresh} className="btn btn-ghost btn-xs btn-circle" title="Làm mới">
              <RefreshCw className="w-3.5 h-3.5" />
            </button>
            <button onClick={() => setShowFilter(!showFilter)} className={`btn btn-xs btn-circle ${showFilter ? "btn-primary" : "btn-ghost"}`}>
              <Filter className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>

        {showFilter && (
          <div className="max-w-6xl mx-auto mt-2 p-3 bg-base-100 rounded-xl border border-base-300 shadow">
            <label className="flex items-center gap-3 cursor-pointer">
              <input type="checkbox" className="checkbox checkbox-primary checkbox-sm" checked={onlineOnly} onChange={(e) => setOnlineOnly(e.target.checked)} />
              <span className="text-sm">Chỉ hiện người đang online</span>
              <span className="badge badge-success badge-sm">{Math.max(0, onlineUsers.length - 1)} online</span>
            </label>
          </div>
        )}
      </div>

      <div className="max-w-6xl mx-auto px-4 pt-5 pb-12">

        {/* Stats */}
        <div className="grid grid-cols-3 gap-3 mb-5">
          {statsData.map((s) => (
            <div key={s.label} className="bg-base-100 rounded-xl p-3 text-center border border-base-300 shadow-sm">
              <div className={`flex justify-center mb-1 ${s.color}`}>{s.icon}</div>
              <p className={`text-xl font-black ${s.color}`}>{s.value}</p>
              <p className="text-xs text-base-content/50">{s.label}</p>
            </div>
          ))}
        </div>

        {/* Tabs */}
        <div className="flex gap-1 bg-base-100 p-1 rounded-xl border border-base-300 mb-5">
          {tabs.map((t) => (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`flex-1 flex items-center justify-center gap-1.5 py-1.5 rounded-lg text-sm font-medium transition-all
                ${tab === t.key ? "bg-primary text-primary-content shadow" : "text-base-content/60 hover:text-base-content"}`}>
              {t.icon}
              <span className="hidden sm:inline">{t.label}</span>
              {t.badge > 0 && (
                <span className={`badge badge-xs ${tab === t.key ? "bg-white/25 text-white border-0" : "badge-primary"}`}>{t.badge}</span>
              )}
            </button>
          ))}
        </div>

        {/* ════ DISCOVER ════ */}
        {tab === "discover" && (
          <>
            {isLoading ? (
              <div className="grid gap-4" style={{gridTemplateColumns:"repeat(auto-fill,minmax(220px,1fr))"}}>
                {[...Array(8)].map((_, i) => <SkeletonCard key={i} />)}
              </div>
            ) : visibleUsers.length === 0 ? (
              <div className="text-center py-20 space-y-4">
                <div className="w-16 h-16 rounded-full bg-base-100 border-2 border-base-300 flex items-center justify-center mx-auto">
                  <Users className="w-8 h-8 text-base-content/20" />
                </div>
                <p className="font-bold text-base-content/50">Không có ai phù hợp</p>
                <button onClick={handleRefresh} className="btn btn-sm btn-primary rounded-xl gap-2">
                  <RefreshCw className="w-4 h-4" /> Làm mới
                </button>
              </div>
            ) : (
              <>
                <p className="text-xs text-base-content/40 mb-3">{visibleUsers.length} người được đề xuất cho bạn</p>
                <div className="grid gap-4" style={{gridTemplateColumns:"repeat(auto-fill,minmax(220px,1fr))"}}>
                  {visibleUsers.map((u) => (
                    <UserCard
                      key={u.userId}
                      user={u}
                      onLike={handleLike}
                      onSkip={handleSkip}
                      onMessage={handleMessage}
                      isOnline={onlineUsers.includes(u._id)}
                      isLiked={!!likedUsers.find((l) => l.userId === u.userId)}
                      isSkipped={skippedIds.has(u.userId)}
                    />
                  ))}
                </div>
              </>
            )}
          </>
        )}

        {/* ════ LIKED ════ */}
        {tab === "liked" && (
          <div>
            {likedUsers.length === 0 ? (
              <div className="text-center py-20 space-y-3">
                <Heart className="w-10 h-10 text-base-content/20 mx-auto" />
                <p className="text-base-content/40">Bạn chưa thích ai cả</p>
                <button onClick={() => setTab("discover")} className="btn btn-sm btn-primary rounded-xl">Khám phá ngay</button>
              </div>
            ) : (
              <div className="grid gap-2.5 max-w-2xl" style={{gridTemplateColumns:"repeat(auto-fill,minmax(280px,1fr))"}}>
                {likedUsers.map((u) => <LikedRow key={u.userId} user={u} onMessage={handleMessage} />)}
              </div>
            )}
          </div>
        )}

        {/* ════ MATCHES ════ */}
        {tab === "matches" && (
          <div>
            {matches.length === 0 ? (
              <div className="text-center py-20 space-y-3">
                <Star className="w-10 h-10 text-base-content/20 mx-auto" />
                <p className="text-base-content/40">Chưa có match nào</p>
                <button onClick={() => setTab("discover")} className="btn btn-sm btn-primary rounded-xl">Khám phá ngay</button>
              </div>
            ) : (
              <>
                <p className="text-sm text-base-content/50 mb-4">🎉 Bạn có <span className="font-bold text-primary">{matches.length}</span> match mới!</p>
                <div className="grid gap-4" style={{gridTemplateColumns:"repeat(auto-fill,minmax(220px,1fr))"}}>
                  {matches.map((u) => <MatchCard key={u.userId} user={u} onMessage={handleMessage} />)}
                </div>
              </>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default MatchPage;
