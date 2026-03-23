// ============================================================
//  matchApi.js  –  Tất cả API calls cho Match feature
//  TODO: Thay mock bằng axios calls thật khi có backend
// ============================================================
import { MOCK_USERS } from "../../data/mockData";

// TODO: GET /api/match/suggestions
export const fetchSuggestedUsers = async (currentUserId) => {
  await new Promise((r) => setTimeout(r, 400));
  return MOCK_USERS.filter((u) => u.userId !== currentUserId);
};

// TODO: POST /api/match/like/:targetUserId
export const postLike = async (_targetUserId) => {
  await new Promise((r) => setTimeout(r, 150));
  return { isMatch: Math.random() > 0.6 };
};

// TODO: POST /api/match/skip/:targetUserId
export const postSkip = async (_targetUserId) => {
  // no-op for now
};