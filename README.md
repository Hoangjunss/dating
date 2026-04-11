# 💘 Dating App — Ứng dụng Hẹn hò Trực tuyến

Ứng dụng hẹn hò full-stack với hệ thống gợi ý thông minh dựa trên thuật toán ELO, nhắn tin thời gian thực qua WebSocket và xác thực bảo mật bằng JWT.

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Tính năng](#tính-năng)
- [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [API Endpoints](#api-endpoints)
- [Cài đặt & Chạy dự án](#cài-đặt--chạy-dự-án)

---

## 🌟 Tổng quan

Dating App là một ứng dụng web hẹn hò được xây dựng theo mô hình **Client-Server**, gồm:

- **Backend**: RESTful API với Spring Boot, xử lý toàn bộ business logic, bảo mật và lưu trữ dữ liệu.
- **Frontend**: Single Page Application (SPA) với React, mang lại trải nghiệm người dùng mượt mà và hiện đại.

Hệ thống cho phép người dùng tạo hồ sơ cá nhân, vuốt (swipe) để thích hoặc bỏ qua người khác, nhắn tin thời gian thực khi hai bên match, và chia sẻ bài viết trong cộng đồng.

---

## ✨ Tính năng

### 🔐 Xác thực & Bảo mật
- Đăng ký / Đăng nhập bằng email và mật khẩu (mã hóa BCrypt).
- Xác thực stateless bằng **JWT** (Access Token + Refresh Token).
- **Rate Limiting** chống brute-force và spam request.
- Bảo mật WebSocket với JWT trên từng kết nối STOMP.
- Security headers (CSP, X-Frame-Options, Referrer-Policy).

### 👤 Hồ sơ người dùng
- Tạo và cập nhật hồ sơ cá nhân (tên, tuổi, giới thiệu, vị trí...).
- Upload nhiều ảnh đại diện, lưu trữ trên **Cloudinary**.
- Quản lý sở thích (interests) cá nhân.
- Thiết lập preference tìm kiếm (độ tuổi, giới tính, khoảng cách...).

### 💡 Gợi ý & Matching
- Hệ thống gợi ý thông minh lọc theo preference và sở thích chung.
- Thuật toán **ELO Score** để xếp hạng và đề xuất người dùng phù hợp.
- Cơ chế **Swipe** (Like / Dislike) với logic tự động tạo Match khi hai bên cùng thích nhau.
- Xem danh sách Match hiện tại và lịch sử.

### 💬 Nhắn tin thời gian thực
- Chat 1-1 qua **WebSocket (STOMP)** — tin nhắn gửi/nhận ngay lập tức.
- Hỗ trợ gửi ảnh trong tin nhắn.
- Tính năng **thu hồi tin nhắn** (Unsend) theo policy thời gian.
- Theo dõi trạng thái **Online/Offline** của người dùng.

### 📝 Bài viết cộng đồng
- Đăng, xem và tương tác với bài viết (Post).
- Feed bài viết được phân trang.

---

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────┐
│                   React Frontend                    │
│         (Vite + Zustand + Tailwind + DaisyUI)       │
└──────────────┬─────────────────┬───────────────────┘
               │  REST API       │  WebSocket (STOMP)
               ▼                 ▼
┌─────────────────────────────────────────────────────┐
│              Spring Boot Backend                    │
│                                                     │
│  ┌──────────┐  ┌──────────┐  ┌────────────────────┐│
│  │Controller│→ │ Service  │→ │    Repository      ││
│  └──────────┘  └──────────┘  └────────────────────┘│
│                                                     │
│  ┌──────────────────────────────────────────────┐  │
│  │   Security Layer (JWT + Rate Limit Filter)   │  │
│  └──────────────────────────────────────────────┘  │
└──────────┬──────────────────────────┬──────────────┘
           ▼                          ▼
    ┌─────────────┐           ┌──────────────┐
    │  Database   │           │  Cloudinary  │
    │(PostgreSQL) │           │ (Ảnh/Media)  │
    └─────────────┘           └──────────────┘
```

---

## 🛠️ Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 17+ | Ngôn ngữ lập trình chính |
| Spring Boot | 3.x | Framework backend |
| Spring Security | 6.x | Xác thực & phân quyền |
| Spring Data JPA | 3.x | ORM, tương tác database |
| Spring WebSocket | 3.x | Realtime messaging (STOMP) |
| JWT (jjwt) | - | Stateless authentication |
| Cloudinary SDK | - | Lưu trữ & xử lý ảnh |
| Lombok | - | Giảm boilerplate code |
| Maven | 3.9 | Build & quản lý dependency |

### Frontend
| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| React | 19 | UI framework |
| Vite | 8 | Build tool & dev server |
| React Router | 7 | Client-side routing |
| Zustand | 5 | State management |
| Axios | 1.x | HTTP client |
| Socket.IO Client | 4.x | WebSocket client |
| Tailwind CSS | 3.x | Utility-first CSS |
| DaisyUI | 5.x | UI component library |
| React Hot Toast | 2.x | Notification toasts |

---

## 📁 Cấu trúc dự án

```
├── Dating/                          # Spring Boot Backend
│   └── src/main/java/com/example/Dating/
│       ├── config/                  # Cấu hình (Security, WebSocket, CORS, Async)
│       ├── controller/              # REST Controllers & WebSocket Controllers
│       ├── dtos/                    # Request & Response DTOs
│       ├── entities/                # JPA Entities (Database models)
│       ├── events/                  # Application Events (Message unsend...)
│       ├── exception/               # Global Exception Handler
│       ├── filter/                  # Rate Limit Filter
│       ├── mapper/                  # Entity ↔ DTO Mappers
│       ├── policy/                  # Business policies (UnsendPolicy...)
│       ├── repository/              # Spring Data JPA Repositories
│       ├── security/                # JWT Provider, Filter, Entry Points
│       ├── service/                 # Business Logic (Interface + Impl)
│       ├── specification/           # JPA Specifications (dynamic query)
│       └── utils/                   # Cloudinary Service
│
└── DatingFE/dating/                 # React Frontend
    └── src/
        ├── components/              # Reusable UI components
        │   ├── match/               # Swipe card & match logic
        │   └── skeletons/           # Loading skeleton components
        ├── page/                    # Các trang chính (Home, Login, Match, Profile)
        ├── store/                   # Zustand stores (Auth, Chat, Post, Profile, Theme)
        ├── lib/                     # Axios instance & utilities
        └── data/                    # Mock data
```

---

## 🔌 API Endpoints

| Module | Method | Endpoint | Mô tả |
|---|---|---|---|
| **Auth** | POST | `/api/auth/register` | Đăng ký tài khoản |
| | POST | `/api/auth/login` | Đăng nhập |
| | POST | `/api/auth/refresh` | Refresh access token |
| **Profile** | GET | `/api/profiles/{userId}` | Lấy thông tin hồ sơ |
| | POST | `/api/profiles` | Tạo hồ sơ |
| | PUT | `/api/profiles/{userId}` | Cập nhật hồ sơ |
| | GET | `/api/profiles/{userId}/paginated` | Danh sách gợi ý (phân trang) |
| **Photo** | POST | `/api/photos` | Upload ảnh |
| | GET | `/api/photos/user/{userId}` | Lấy ảnh theo user |
| | DELETE | `/api/photos/{id}` | Xóa ảnh |
| **Swipe** | POST | `/api/swipes` | Thực hiện swipe (like/dislike) |
| **Match** | GET | `/api/matches/me` | Danh sách match hiện tại |
| | DELETE | `/api/matches/{matchId}` | Unmatch |
| **Conversation** | GET | `/api/conversations/me` | Danh sách hội thoại |
| | POST | `/api/conversations` | Tạo hội thoại mới |
| **Message** | GET | `/api/messages/{conversationId}` | Lịch sử tin nhắn |
| | DELETE | `/api/messages/{id}` | Thu hồi tin nhắn |
| **Presence** | GET | `/api/presence/{userId}` | Trạng thái online |
| **Post** | GET | `/api/posts` | Danh sách bài viết |
| | POST | `/api/posts` | Tạo bài viết |
| **Recommend** | GET | `/api/recommendations` | Danh sách gợi ý theo ELO |
| **WebSocket** | STOMP | `/ws` | Kết nối WebSocket |
| | PUB | `/app/chat.send` | Gửi tin nhắn |
| | SUB | `/topic/messages/{conversationId}` | Nhận tin nhắn |

---

## 🚀 Cài đặt & Chạy dự án

### Yêu cầu
- Java 17+
- Maven 3.9+
- Node.js 18+
- PostgreSQL
- Tài khoản Cloudinary

### Backend

```bash
# Di chuyển vào thư mục backend
cd Dating

# Cấu hình file môi trường
cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
# Điền thông tin database, JWT secret, Cloudinary API key...

# Build và chạy
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# Server khởi động tại: http://localhost:8080
```

### Frontend

```bash
# Di chuyển vào thư mục frontend
cd DatingFE/dating

# Cài đặt dependencies
npm install

# Chạy development server
npm run dev
# Ứng dụng chạy tại: http://localhost:5173
```

---

## 👨‍💻 Tác giả

**Hoàng Junss**
- GitHub: [@Hoangjunss](https://github.com/Hoangjunss)
