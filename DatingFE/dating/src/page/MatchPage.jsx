import { Heart, Users, Star, Zap, RefreshCw, Filter, Sparkles, Loader2 } from "lucide-react";
import useMatch from "../components/match/UseMatch";
import UserCard from "../components/match/UserCard";
import MatchCard from "../components/match/MatchCard";
import { LikedRow } from "../components/match/MatchHelpers";
import MatchSkeleton from "../components/skeletons/MatchSkeleton";

const MatchPage = () => {
  const {
    visibleUsers, likedUsers, matches, onlineUsers, isLoading,
    isLoadingMore, totalElements, hasMore,
    tab, setTab,
    showFilter, setShowFilter,
    onlineOnly, setOnlineOnly,
    handleLike, handleSkip, handleMessage, handleRefresh, handleLoadMore,
  } = useMatch();

  const tabs = [
    { key: "discover", label: "Khám phá", icon: <Zap   className="w-4 h-4" /> },
    { key: "liked",    label: "Đã thích", icon: <Heart className="w-4 h-4" />, badge: likedUsers.length },
    { key: "matches",  label: "Matches",  icon: <Star  className="w-4 h-4" />, badge: matches.length   },
  ];
  const stats = [
    { label: "Đề xuất",  value: totalElements,      color: "text-primary",   icon: <Users className="w-4 h-4" /> },
    { label: "Đã thích", value: likedUsers.length,   color: "text-pink-500",  icon: <Heart className="w-4 h-4" /> },
    { label: "Matched",  value: matches.length,      color: "text-amber-500", icon: <Star  className="w-4 h-4" /> },
  ];

  return (
    <div className="min-h-screen bg-base-200 pt-20">
      {/* ── Header ── */}
      <div className="max-w-6xl mx-auto px-4 pt-5 pb-3 flex items-center justify-between">
        <div className="flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-2xl bg-gradient-to-br from-pink-500 to-rose-500 flex items-center justify-center shadow-lg shadow-rose-500/30">
            <Sparkles className="w-4 h-4 text-white" />
          </div>
          <div>
            <h1 className="text-lg font-bold leading-tight">Khám Phá</h1>
            <p className="text-xs text-base-content/40">Tìm bạn mới hôm nay</p>
          </div>
        </div>
        <div className="flex items-center gap-1.5">
          <button onClick={handleRefresh} className="btn btn-ghost btn-sm btn-circle" title="Làm mới">
            <RefreshCw className="w-4 h-4" />
          </button>
          <button
            onClick={() => setShowFilter(!showFilter)}
            className={`btn btn-sm btn-circle ${showFilter ? "btn-primary" : "btn-ghost"}`}
          >
            <Filter className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* ── Filter panel ── */}
      {showFilter && (
        <div className="max-w-6xl mx-auto px-4 pb-3">
          <div className="p-4 bg-base-100 rounded-2xl border border-base-300 shadow-md">
            <p className="text-sm font-semibold mb-2">Bộ lọc</p>
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                className="checkbox checkbox-primary checkbox-sm"
                checked={onlineOnly}
                onChange={(e) => setOnlineOnly(e.target.checked)}
              />
              <span className="text-sm">Chỉ hiện người đang online</span>
              <span className="badge badge-success badge-sm">
                {Math.max(0, onlineUsers.length - 1)} online
              </span>
            </label>
          </div>
        </div>
      )}

      <div className="max-w-6xl mx-auto px-4 pb-14">
        {/* ── Stats ── */}
        <div className="grid grid-cols-3 gap-3 mb-5">
          {stats.map((s) => (
            <div key={s.label} className="bg-base-100 rounded-2xl p-3 text-center border border-base-300 shadow-sm">
              <div className={`flex justify-center mb-1 ${s.color}`}>{s.icon}</div>
              <p className={`text-xl font-black ${s.color}`}>{s.value}</p>
              <p className="text-xs text-base-content/50">{s.label}</p>
            </div>
          ))}
        </div>

        {/* ── Tabs ── */}
        <div className="flex gap-1 bg-base-100 p-1 rounded-2xl border border-base-300 mb-6 shadow-sm">
          {tabs.map((t) => (
            <button
              key={t.key}
              onClick={() => setTab(t.key)}
              className={`flex-1 flex items-center justify-center gap-1.5 py-2 rounded-xl text-sm font-semibold transition-all duration-200
                ${tab === t.key
                  ? "bg-gradient-to-r from-pink-500 to-rose-500 text-white shadow-md shadow-rose-500/30"
                  : "text-base-content/60 hover:text-base-content hover:bg-base-200"
                }`}
            >
              {t.icon}
              {t.label}
              {t.badge > 0 && (
                <span className={`badge badge-xs ${tab === t.key ? "bg-white/25 text-white border-0" : "badge-error"}`}>
                  {t.badge}
                </span>
              )}
            </button>
          ))}
        </div>

        {/* ════ Tab: DISCOVER ════ */}
        {tab === "discover" && (
          isLoading ? (
            <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))" }}>
              {[...Array(8)].map((_, i) => <MatchSkeleton key={i} />)}
            </div>
          ) : visibleUsers.length === 0 ? (
            <div className="text-center py-28 space-y-4">
              <div className="w-16 h-16 rounded-full bg-base-100 border border-base-300 flex items-center justify-center mx-auto">
                <Users className="w-8 h-8 text-base-content/20" />
              </div>
              <p className="font-semibold text-base-content/40">Không có ai phù hợp</p>
              <button onClick={handleRefresh} className="btn btn-sm btn-primary rounded-2xl gap-2">
                <RefreshCw className="w-4 h-4" /> Làm mới
              </button>
            </div>
          ) : (
            <>
              <div className="flex items-center gap-3 mb-4">
                <div className="flex items-center gap-2 bg-base-100 border border-base-300 rounded-full px-3 py-1.5 shadow-sm">
                  <span className="w-2 h-2 rounded-full bg-primary animate-pulse" />
                  <span className="text-xs font-semibold text-base-content/70">
                    {visibleUsers.length} gợi ý hôm nay
                  </span>
                </div>
                <div className="h-px flex-1 bg-base-300" />
              </div>

              <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))" }}>
                {visibleUsers.map((u) => (
                  <UserCard
                    key={u.userId}
                    user={u}
                    onLike={handleLike}
                    onSkip={handleSkip}
                    onMessage={handleMessage}
                    isOnline={onlineUsers.includes(u.userId)}
                  />
                ))}
              </div>

              {/* ── Load More ── */}
              {hasMore && (
                <div className="flex justify-center mt-8">
                  <button
                    onClick={handleLoadMore}
                    disabled={isLoadingMore}
                    className="btn btn-outline btn-primary rounded-2xl gap-2 px-8"
                  >
                    {isLoadingMore ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        Đang tải...
                      </>
                    ) : (
                      <>
                        <Sparkles className="w-4 h-4" />
                        Xem thêm gợi ý
                      </>
                    )}
                  </button>
                </div>
              )}
            </>
          )
        )}

        {/* ════ Tab: LIKED ════ */}
        {tab === "liked" && (
          likedUsers.length === 0 ? (
            <div className="text-center py-28 space-y-3">
              <Heart className="w-12 h-12 text-base-content/20 mx-auto" />
              <p className="text-base-content/40 font-medium">Bạn chưa thích ai cả</p>
              <button onClick={() => setTab("discover")} className="btn btn-sm btn-primary rounded-2xl">
                Khám phá ngay
              </button>
            </div>
          ) : (
            <div className="space-y-2 max-w-2xl">
              {likedUsers.map((u) => (
                <LikedRow key={u.userId} user={u} onMessage={handleMessage} />
              ))}
            </div>
          )
        )}

        {/* ════ Tab: MATCHES ════ */}
        {tab === "matches" && (
          matches.length === 0 ? (
            <div className="text-center py-28 space-y-3">
              <Star className="w-12 h-12 text-base-content/20 mx-auto" />
              <p className="text-base-content/40 font-medium">Chưa có match nào</p>
              <button onClick={() => setTab("discover")} className="btn btn-sm btn-primary rounded-2xl">
                Khám phá ngay
              </button>
            </div>
          ) : (
            <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(180px, 1fr))" }}>
              {matches.map((u) => (
                <MatchCard key={u.userId} user={u} onMessage={handleMessage} />
              ))}
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default MatchPage;