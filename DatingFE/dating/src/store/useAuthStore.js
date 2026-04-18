import { create } from "zustand";
import {
  axiosInstance,
  getStoredAccessToken,
  setStoredAccessToken,
  clearStoredAccessToken,
} from "../lib/axios.js";
import toast from "react-hot-toast";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { persist, createJSONStorage } from "zustand/middleware";

/** Cùng origin với API (axios baseURL = `${WS_ORIGIN}/api`) */
const WS_ORIGIN = "http://localhost:8080";

const extractTokenFromAuthResponse = (res) => {
  const bodyToken =
    res?.data?.accessToken ||
    res?.data?.token ||
    res?.data?.jwt ||
    res?.data?.data?.accessToken ||
    res?.data?.data?.token ||
    null;

  if (bodyToken) return bodyToken;

  const authHeader = res?.headers?.authorization || res?.headers?.Authorization;
  if (!authHeader) return null;

  return authHeader.startsWith("Bearer ") ? authHeader.slice(7) : authHeader;
};

const extractUserFromAuthResponse = (res) => {
  return res?.data?.user || res?.data?.data?.user || res?.data;
};

export const useAuthStore = create(
  persist(
    (set, get) => ({
      authUser: null,
      isSigningUp: false,
      isLoggingIn: false,
      isUpdatingProfile: false,
      isCheckingAuth: true,
      onlineUsers: [],
      stompClient: null,
      stompConnected: false,

      checkAuth: async () => {
        try {
          const res = await axiosInstance.get("/users/me");
          const user = extractUserFromAuthResponse(res);
          const token = extractTokenFromAuthResponse(res);

          if (token) {
            setStoredAccessToken(token);
          }

          set({ authUser: user });
          get().connectSocket();
        } catch (error) {
          const status = error?.response?.status;
          if (status && status !== 401) {
            console.log("Error in checkAuth:", error);
          }
          clearStoredAccessToken();
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
          const user = extractUserFromAuthResponse(res);
          const token = extractTokenFromAuthResponse(res);

          if (token) {
            setStoredAccessToken(token);
          }

          set({ authUser: user });
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
          const user = extractUserFromAuthResponse(res);
          const token = extractTokenFromAuthResponse(res);

          if (token) {
            setStoredAccessToken(token);
          }

          set({ authUser: user });
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
          clearStoredAccessToken();
          set({ authUser: null });
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

      /**
       * STOMP + SockJS — khớp Spring WebSocketSecurityConfig:
       * SockJS endpoint /ws, JWT trong CONNECT header Authorization: Bearer + access token
       */
      connectSocket: () => {
        const { authUser, stompClient: existing } = get();
        const token = getStoredAccessToken();

        if (!authUser || !token) return;

        if (existing?.active) return;

        get().disconnectSocket();

        const client = new Client({
          webSocketFactory: () => new SockJS(`${WS_ORIGIN}/ws`),
          connectHeaders: {
            Authorization: `Bearer ${token}`,
          },
          reconnectDelay: 5000,
          heartbeatIncoming: 4000,
          heartbeatOutgoing: 4000,
          onConnect: () => {
            set({ stompConnected: true });
          },
          onDisconnect: () => {
            set({ stompConnected: false });
          },
          onWebSocketClose: () => {
            set({ stompConnected: false });
          },
          onStompError: (frame) => {
            console.error("[STOMP]", frame.headers["message"], frame.body);
            toast.error("Lỗi kết nối chat (STOMP)");
          },
        });

        set({ stompClient: client });
        client.activate();
      },

      disconnectSocket: () => {
        const client = get().stompClient;
        if (client) {
          client.deactivate();
          set({ stompClient: null, stompConnected: false });
        }
      },
    }),
    {
      name: "auth-storage",
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({ authUser: state.authUser }),
    }
  )
);
