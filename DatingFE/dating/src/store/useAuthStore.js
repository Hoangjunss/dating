import { create } from "zustand";
import { axiosInstance } from "../lib/axios.js";
import toast from "react-hot-toast";
import { io } from "socket.io-client";
import { persist, createJSONStorage } from "zustand/middleware";

const BASE_URL =   "http://localhost:8080/api/v1";

export const useAuthStore = create(
  persist(
    (set, get) => ({
      authUser: null,
      isSigningUp: false,
      isLoggingIn: false,
      isUpdatingProfile: false,
      isCheckingAuth: true,
      onlineUsers: [],
      socket: null,

      // 1. Kiểm tra Auth (Kết hợp gọi API để làm mới data)
      checkAuth: async () => {
        try {
          const res = await axiosInstance.get("/auth/check");
          set({ authUser: res.data });
          get().connectSocket();
        } catch (error) {
          console.log("Error in checkAuth:", error);
          // Nếu API báo lỗi (token hết hạn), xóa user để bắt login lại
          set({ authUser: null });
          get().disconnectSocket();
        } finally {
          set({ isCheckingAuth: false });
        }
      },

      signup: async (data) => {
        set({ isSigningUp: true });
        try {
          const res = await axiosInstance.post("/auth/register", data);
          set({ authUser: res.data });
          toast.success("Account created successfully");
          get().connectSocket();
        } catch (error) {
          toast.error(error.response?.data?.message || "Signup failed");
        } finally {
          set({ isSigningUp: false });
        }
      },

      login: async (data) => {
        set({ isLoggingIn: true });
        try {
          const res = await axiosInstance.post("/auth/login", data);
          set({ authUser: res.data }); // Tự động lưu vào localStorage nhờ persist
          toast.success("Logged in successfully");
          get().connectSocket();
        } catch (error) {
          toast.error(error.response?.data?.message || "Login failed");
        } finally {
          set({ isLoggingIn: false });
        }
      },

      logout: async () => {
        try {
          await axiosInstance.post("/auth/logout");
          set({ authUser: null }); // Tự động xóa khỏi localStorage
          toast.success("Logged out successfully");
          get().disconnectSocket();
        } catch (error) {
          toast.error(error.response?.data?.message || "Logout failed");
        }
      },

      updateProfile: async (data) => {
        set({ isUpdatingProfile: true });
        try {
          const res = await axiosInstance.put("/auth/update-profile", data);
          set({ authUser: res.data });
          toast.success("Profile updated successfully");
        } catch (error) {
          console.log("error in update profile:", error);
          toast.error(error.response?.data?.message || "Update failed");
        } finally {
          set({ isUpdatingProfile: false });
        }
      },

      // 2. Quản lý Socket
      connectSocket: () => {
        const { authUser, socket: existingSocket } = get();
        
        // Nếu không có user hoặc socket đang chạy rồi thì thôi
        if (!authUser || existingSocket?.connected) return;

        // Lưu ý: Sử dụng authUser.userId (vì JSON của bạn dùng key này)
        const socket = io(BASE_URL, {
          query: {
            userId: authUser.userId || authUser._id, 
          },
        });

        socket.connect();
        set({ socket: socket });

        socket.on("getOnlineUsers", (userIds) => {
          set({ onlineUsers: userIds });
        });
      },

      disconnectSocket: () => {
        if (get().socket?.connected) {
          get().socket.disconnect();
          set({ socket: null });
        }
      },
    }),
    {
      name: "auth-storage", // Key lưu dưới LocalStorage
      storage: createJSONStorage(() => localStorage),
      // Chỉ lưu authUser vào localStorage, các trạng thái loading không cần lưu
      partialize: (state) => ({ authUser: state.authUser }),
    }
  )
);