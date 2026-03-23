// src/store/usePostStore.js
import { create } from "zustand";
import { axiosInstance } from "../lib/axios";
import toast from "react-hot-toast";

export const usePostStore = create((set, get) => ({
  posts: [],
  isPostsLoading: false,

  getPostsByUserId: async (userId, page = 0, size = 10) => {
    set({ isPostsLoading: true });
    try {
      // Khớp với @GetMapping("/user/{userId}") của Dung
      const res = await axiosInstance.get(`/posts/user/${userId}`, {
        params: { page, size }
      });
      
      // QUAN TRỌNG: Backend trả về Page nên phải lấy .content
      set({ posts: res.data.content }); 
      
    } catch (error) {
      toast.error("Không thể tải bài đăng");
      console.error(error);
    } finally {
      set({ isPostsLoading: false });
    }
  },

  // Hàm tạo bài mới cũng cần khớp DTO trả về
  createPost: async (postData) => {
    try {
      const res = await axiosInstance.post("/posts", postData);
      // res.data lúc này là một PostResponse đơn lẻ
      set({ posts: [res.data, ...get().posts] }); 
      toast.success("Đã đăng bài! ✨");
    } catch (error) {
      toast.error("Lỗi khi đăng bài");
    }
  }
}));