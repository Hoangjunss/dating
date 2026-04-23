import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";
import { useChatStore } from "../../store/useChatStore";
import { fetchSuggestedUsers, postSwipe, postSkip } from "./matchApi";
import toast from "react-hot-toast";

const PAGE_SIZE = 10;

const useMatch = () => {
  const { authUser, onlineUsers = [] } = useAuthStore();
  const { setSelectedUser } = useChatStore();
  const navigate = useNavigate();

  // ── Danh sách ứng viên từ API ──
  const [allUsers,      setAllUsers]      = useState([]);
  const [isLoading,     setIsLoading]     = useState(true);
  const [isLoadingMore, setIsLoadingMore] = useState(false);

  // ── Pagination ──
  const [currentPage,  setCurrentPage]  = useState(0);
  const [totalPages,   setTotalPages]   = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // ── Interaction state ──
  const [likedUsers, setLikedUsers] = useState([]);
  const [skippedIds, setSkippedIds] = useState(new Set());
  const [matches,    setMatches]    = useState([]);

  // ── UI state ──
  const [tab,        setTab]        = useState("discover");
  const [showFilter, setShowFilter] = useState(false);
  const [onlineOnly, setOnlineOnly] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);

  // ── Load trang đầu ──
  useEffect(() => {
    const load = async () => {
      setIsLoading(true);
      setAllUsers([]);
      setCurrentPage(0);
      try {
        const data = await fetchSuggestedUsers({ page: 0, size: PAGE_SIZE });
        setAllUsers(data.content ?? []);
        setTotalPages(data.totalPages ?? 1);
        setTotalElements(data.totalElements ?? 0);
        setCurrentPage(0);
      } catch {
        toast.error("Không thể tải danh sách gợi ý");
      } finally {
        setIsLoading(false);
      }
    };
    load();
  }, [authUser?.userId, refreshKey]);

  // ── Load thêm (pagination) ──
  const handleLoadMore = useCallback(async () => {
    const nextPage = currentPage + 1;
    if (nextPage >= totalPages || isLoadingMore) return;
    setIsLoadingMore(true);
    try {
      const data = await fetchSuggestedUsers({ page: nextPage, size: PAGE_SIZE });
      setAllUsers((prev) => {
        // Loại trùng userId
        const existingIds = new Set(prev.map((u) => u.userId));
        const newItems = (data.content ?? []).filter((u) => !existingIds.has(u.userId));
        return [...prev, ...newItems];
      });
      setCurrentPage(nextPage);
      setTotalPages(data.totalPages ?? totalPages);
    } catch {
      toast.error("Không thể tải thêm gợi ý");
    } finally {
      setIsLoadingMore(false);
    }
  }, [currentPage, totalPages, isLoadingMore]);

  // ── Lọc online ──
  const visibleUsers = allUsers.filter((u) =>
    onlineOnly ? onlineUsers.includes(u.userId) : true
  );

  // ── Thích ──
  const handleLike = async (user) => {
    if (likedUsers.find((u) => u.userId === user.userId)) return null;
    setLikedUsers((p) => [...p, user]);

    const { isMatch } = await postSwipe(user.userId, true);
    if (isMatch) {
      setMatches((p) => [...p, user]);
    }
    return { matched: isMatch, user };
  };

  // ── Bỏ qua ──
  const handleSkip = async (user) => {
    setSkippedIds((p) => new Set([...p, user.userId]));
    await postSwipe(user.userId, false);
  };

  // ── Nhắn tin ──
  const handleMessage = (user) => {
    setSelectedUser(user);
    navigate("/");
  };

  // ── Làm mới toàn bộ ──
  const handleRefresh = () => {
    setRefreshKey((k) => k + 1);
    setLikedUsers([]);
    setSkippedIds(new Set());
    setMatches([]);
    toast.success("Đã làm mới!");
  };

  const hasMore = currentPage + 1 < totalPages;

  return {
    visibleUsers,
    likedUsers,
    matches,
    onlineUsers,
    isLoading,
    isLoadingMore,
    totalElements,
    hasMore,
    tab, setTab,
    showFilter, setShowFilter,
    onlineOnly, setOnlineOnly,
    handleLike,
    handleSkip,
    handleMessage,
    handleRefresh,
    handleLoadMore,
  };
};

export default useMatch;