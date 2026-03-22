// ============================================================
//  MatchSkeleton.jsx  –  Loading skeleton cho MatchPage
//  Đặt trong: src/components/skeletons/MatchSkeleton.jsx
// ============================================================

const MatchCardSkeleton = () => (
  <div
    className="rounded-3xl overflow-hidden animate-pulse bg-base-100 shadow-md"
    style={{ aspectRatio: "3/4" }}
  >
    <div className="w-full h-full bg-base-300" />
  </div>
);

const MatchSkeleton = () => (
  <div>
    {/* Stats skeleton */}
    <div className="grid grid-cols-3 gap-3 mb-5">
      {[...Array(3)].map((_, i) => (
        <div key={i} className="bg-base-100 rounded-2xl p-3 text-center border border-base-300 animate-pulse">
          <div className="w-5 h-5 bg-base-300 rounded-full mx-auto mb-2" />
          <div className="h-6 w-10 bg-base-300 rounded mx-auto mb-1" />
          <div className="h-3 w-12 bg-base-300 rounded mx-auto" />
        </div>
      ))}
    </div>

    {/* Tabs skeleton */}
    <div className="flex gap-1 bg-base-100 p-1 rounded-2xl border border-base-300 mb-6">
      {[...Array(3)].map((_, i) => (
        <div key={i} className="flex-1 h-9 bg-base-300 rounded-xl animate-pulse" />
      ))}
    </div>

    {/* Cards grid skeleton */}
    <div className="grid gap-4" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(200px, 1fr))" }}>
      {[...Array(8)].map((_, i) => (
        <MatchCardSkeleton key={i} />
      ))}
    </div>
  </div>
);

export default MatchSkeleton;
