// ============================================================
//  useMatch.js  –  Toàn bộ state & logic của MatchPage
//  Không chứa JSX — hook thuần JS, dễ test và tái sử dụng
// ============================================================
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";
import { useChatStore } from "../../store/useChatStore";
import { MOCK_CURRENT_USER, MOCK_ONLINE_USERS } from "../../data/mockData";
import { fetchSuggestedUsers, postLike, postSkip } from "./matchApi";
import toast from "react-hot-toast";

const useMatch = () => {
  // ── TODO: Bỏ fallback MOCK_* khi store đã có data thật ──
  const { authUser: storeAuth, onlineUsers: storeOnline } = useAuthStore();
  const authUser    = storeAuth   || MOCK_CURRENT_USER;
  const onlineUsers = storeOnline?.length ? storeOnline : MOCK_ONLINE_USERS;
  // ────────────────────────────────────────────────────────

  const { setSelectedUser } = useChatStore();
  const navigate = useNavigate();

  const [allUsers,   setAllUsers]   = useState([]);
  const [isLoading,  setIsLoading]  = useState(true);
  const [likedUsers, setLikedUsers] = useState([]);
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
        toast.error("Không thể tải danh sách");
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, [authUser?.userId, refreshKey]);

  const visibleUsers = allUsers.filter((u) =>
    onlineOnly ? onlineUsers.includes(u._id) : true
  );

  // Thích — trả về kết quả để MatchPage tự hiện toast (tránh JSX trong hook)
  const handleLike = async (user) => {
    if (likedUsers.find((u) => u.userId === user.userId)) return null;
    setLikedUsers((p) => [...p, user]);

    const { isMatch } = await postLike(user.userId);
    if (isMatch) {
      setMatches((p) => [...p, user]);
    }
    return { matched: isMatch, user };
  };

  const handleSkip = async (user) => {
    setSkippedIds((p) => new Set([...p, user.userId]));
    await postSkip(user.userId);
  };

  const handleMessage = (user) => {
    setSelectedUser(user);
    navigate("/");
  };

  const handleRefresh = () => {
    setRefreshKey((k) => k + 1);
    setLikedUsers([]);
    setSkippedIds(new Set());
    setMatches([]);
    toast.success("Đã làm mới!");
  };

  return {
    visibleUsers, likedUsers, matches, onlineUsers, isLoading,
    tab, setTab,
    showFilter, setShowFilter,
    onlineOnly, setOnlineOnly,
    handleLike, handleSkip, handleMessage, handleRefresh,
  };
};

export default useMatch;