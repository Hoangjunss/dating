import { create } from "zustand";
import { axiosInstance } from "../lib/axios.js";
import toast from "react-hot-toast";

export const useProfileStore = create((set, get) => ({
    userProfile: null, // Profile của chính người dùng đang đăng nhập
    profiles: [],      // Danh sách profiles để quẹt match
    totalPages: 0,
    isProfileLoading: false,
    isUpdatingProfile: false,

    // 1. Lấy profile của chính mình (theo userId)
    getMyProfile: async (userId) => {
        if (!userId) return;
        set({ isProfileLoading: true });
        try {
            const res = await axiosInstance.get(`/profiles/${userId}`);
            set({ userProfile: res.data });
        } catch (error) {
            console.error("Error in getMyProfile:", error);
            // Không toast lỗi ở đây vì có thể user chưa tạo profile bước 2
        } finally {
            set({ isProfileLoading: false });
        }
    },

    // 2. Tạo mới profile (Bước 2 sau khi đăng ký)
    createProfile: async (profileData) => {
        set({ isUpdatingProfile: true });
        try {
            const res = await axiosInstance.post("/profiles", profileData);
            set({ userProfile: res.data });
            toast.success("Hồ sơ đã được tạo thành công!");
            return res.data;
        } catch (error) {
            toast.error(error.response?.data?.message || "Tạo hồ sơ thất bại");
        } finally {
            set({ isUpdatingProfile: false });
        }
    },

    // 3. Cập nhật profile (Dùng cho trang Profile của Dung)
    updateProfile: async (userId, updateData) => {
        set({ isUpdatingProfile: true });
        try {
            const res = await axiosInstance.put(`/profiles/${userId}`, updateData);
            set({ userProfile: res.data });
            toast.success("Cập nhật hồ sơ thành công!");
        } catch (error) {
            toast.error(error.response?.data?.message || "Cập nhật thất bại");
        } finally {
            set({ isUpdatingProfile: false });
        }
    },

    // 4. Lấy danh sách profile để quẹt match (Có phân trang)
    // Khớp với GET /api/profiles/{userId}/paginated
    getMatchProfiles: async (userId, page = 0, size = 10) => {
        if (!userId) return;
        set({ isProfileLoading: true });
        try {
            const res = await axiosInstance.get(`/profiles/${userId}/paginated`, {
                params: { page, size }
            });
            // Spring Boot Page object trả về nội dung trong field 'content'
            set({ 
                profiles: res.data.content, 
                totalPages: res.data.totalPages 
            });
        } catch (error) {
            toast.error("Không thể tải danh sách gợi ý");
        } finally {
            set({ isProfileLoading: false });
        }
    },

    // 5. Xóa profile
    deleteProfile: async (userId) => {
        try {
            await axiosInstance.delete(`/profiles/${userId}`);
            set({ userProfile: null });
            toast.success("Đã xóa hồ sơ");
        } catch (error) {
            toast.error("Xóa hồ sơ thất bại");
        }
    }
}));