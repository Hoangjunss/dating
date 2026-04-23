// ============================================================
//  matchApi.jsx  –  API calls cho Match / Recommendation feature
// ============================================================
import { axiosInstance } from "../../lib/axios";

/**
 * GET /api/recommendations/me?page=0&size=10
 * Trả về Page<CandidateResponse>
 */
export const fetchSuggestedUsers = async ({ page = 0, size = 10 } = {}) => {
  const res = await axiosInstance.get("/recommendations/me", {
    params: { page, size },
  });
  // res.data là Spring Page object: { content: [...], totalPages, totalElements, number, ... }
  return res.data;
};

// TODO: POST /api/match/like/:targetUserId
export const postSwipe = async (toUserId, isLiked) => {
  const res = await axiosInstance.post('/swipes', {
    toUserId,
    isLiked
  });

  return res.data; // SwipeResultResponse
};

// TODO: POST /api/match/skip/:targetUserId
export const postSkip = async (_targetUserId) => {
  // Uncomment khi có endpoint
  // await axiosInstance.post(`/match/skip/${_targetUserId}`);
};