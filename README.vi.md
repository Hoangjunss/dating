# 💘 Dating App — Ứng dụng Hẹn hò Trực tuyến

<div align="right">
  <a href="./README.md">🇬🇧 English</a>
</div>

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-green)
![JWT](https://img.shields.io/badge/JWT-0.12.6-black)

Ứng dụng hẹn hò full-stack với thuật toán gợi ý thông minh dựa trên **ELO Score + Jaccard Similarity**, nhắn tin thời gian thực qua **WebSocket/STOMP** và bảo mật đa lớp bằng **JWT + Rate Limiting**.

---

## 📋 Mục lục

- [Tổng quan](#tổng-quan)
- [Tính năng](#tính-năng)
- [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
- [Thuật toán Recommendation](#thuật-toán-recommendation)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [API Endpoints](#api-endpoints)
- [Cài đặt & Chạy dự án](#cài-đặt--chạy-dự-án)

---

## 🌟 Tổng quan

Dating App là ứng dụng web hẹn hò được xây dựng theo mô hình **Client-Server**, gồm:

- **Backend**: RESTful API với Spring Boot 4, xử lý toàn bộ business logic, thuật toán matching, bảo mật và real-time messaging.
- **Frontend**: Single Page Application với React 19 + Zustand + DaisyUI, mang lại trải nghiệm swipe mượt mà và chat thời gian thực.

Điểm khác biệt so với các dating app thông thường: hệ thống **không chỉ lọc theo preference** mà còn xếp hạng ứng viên qua thuật toán **composite scoring** (ELO + sở thích chung + khoảng cách + hoạt động gần đây), đảm bảo gợi ý luôn đa dạng và phù hợp nhất.

---

## ✨ Tính năng

### 🔐 Xác thực & Bảo mật
- Đăng ký / Đăng nhập bằng email + mật khẩu (BCrypt).
- **JWT stateless**: Access Token (ngắn hạn) + Refresh Token — tự động renew khi hết hạn.
- **WebSocket Security**: JWT được validate ngay tại STOMP CONNECT header — không cho phép kết nối anonymous.
- **Rate Limiting** phân tầng (Bucket4j) theo IP:
  - API thông thường: 100 req/phút/IP
  - Auth endpoints (`/api/auth/**`): 10 req/phút/IP — chống brute-force

### 👤 Hồ sơ người dùng
- Tạo và cập nhật hồ sơ cá nhân (tên hiển thị, giới tính, ngày sinh, thành phố, bio, tọa độ GPS).
- Upload nhiều ảnh, sắp xếp thứ tự ảnh (`sortOrder`) — lưu trữ trên **Cloudinary**.
- Quản lý danh sách sở thích cá nhân (Interests).
- Thiết lập Preference tìm kiếm: giới tính mong muốn, độ tuổi (min/max), khoảng cách tối đa (km).

### 💡 Gợi ý thông minh & Matching
- **Composite scoring** với 4 tín hiệu độc lập (xem [chi tiết bên dưới](#thuật-toán-recommendation)).
- **ELO Score** cập nhật động sau mỗi lượt swipe.
- **Diversity Filter**: tránh gợi ý liên tiếp nhiều người cùng thành phố.
- Fallback candidates khi pool gợi ý < 10 người.
- Cơ chế **Swipe** (Like / Dislike) — tự động tạo Match + Conversation khi mutual like.

### 💬 Nhắn tin thời gian thực
- Chat 1-1 qua **WebSocket/STOMP** — latency thực tế dưới 50ms (local, 50 concurrent connections).
- Gửi **ảnh trong tin nhắn** (upload Cloudinary).
- **Thu hồi tin nhắn** (`Unsend for everyone`) — cấu hình cửa sổ thời gian linh hoạt qua `UnsendPolicy` (mặc định 24 giờ).
- **Xóa tin nhắn phía mình** (`Delete for me`) — không ảnh hưởng phía đối phương.
- Theo dõi trạng thái **Online/Offline** theo thời gian thực.

### 📝 Bài viết cộng đồng
- Tạo, xem, xóa bài viết (Post) với hình ảnh.
- Feed bài viết phân trang.

### 🔌 WebSocket Channels
| Channel | Hướng | Mô tả |
|---|---|---|
| `/ws` | Connect | Điểm kết nối STOMP |
| `/app/chat.send` | Client → Server | Gửi tin nhắn text |
| `/app/chat.photo` | Client → Server | Gửi ảnh |
| `/topic/messages/{conversationId}` | Server → Client | Nhận tin nhắn |
| `/user/{userId}/queue/notifications` | Server → Client | Thông báo match mới |

---

## 🏗️ Kiến trúc hệ thống

```
┌──────────────────────────────────────────────────────────┐
│              React Frontend (Vite, Port 5173)            │
│    React 19 + Zustand + DaisyUI + Tailwind + Socket.IO   │
└──────────────────┬──────────────────────┬────────────────┘
                   │  REST API            │  WebSocket (STOMP)
                   ▼                      ▼
┌──────────────────────────────────────────────────────────┐
│            Spring Boot Backend (Port 8080)               │
│                                                          │
│  ┌──────────────────────────────────────────────────┐   │
│  │         Security & Filter Layer                  │   │
│  │  JwtFilter │ RateLimitFilter(Bucket4j) │ CORS    │   │
│  └──────────────────────────────────────────────────┘   │
│                                                          │
│  ┌────────────┐  ┌──────────────┐  ┌─────────────────┐ │
│  │ REST       │  │  WebSocket   │  │ Recommendation  │ │
│  │ Controllers│  │  Controllers │  │ Engine (ELO)    │ │
│  └─────┬──────┘  └──────┬───────┘  └────────┬────────┘ │
│        └────────────────┴───────────────────┘          │
│                          │                              │
│               ┌──────────▼──────────┐                  │
│               │   Service Layer     │                  │
│               │  + UnsendPolicy     │                  │
│               │  + AsyncEventBus    │                  │
│               └──────────┬──────────┘                  │
│                          │                              │
│               ┌──────────▼──────────┐                  │
│               │  JPA Repositories   │                  │
│               │  + Specifications   │                  │
│               └──────────┬──────────┘                  │
└──────────────────────────┼──────────────────────────────┘
                           │
              ┌────────────┴─────────────┐
              ▼                          ▼
       ┌─────────────┐           ┌──────────────┐
       │    MySQL    │           │  Cloudinary  │
       │  (Port 3306)│           │  (Ảnh/Media) │
       └─────────────┘           └──────────────┘
```

---

## 🧠 Thuật toán Recommendation

Mỗi candidate được chấm điểm theo **4 thành phần độc lập**, kết hợp thành điểm tổng hợp:

```
compositeScore = 0.40 × interestScore
              + 0.35 × eloScore
              + 0.15 × distanceScore
              + 0.10 × activityScore
              + rand(0, 0.05)          ← thêm yếu tố ngẫu nhiên nhỏ
```

| Thành phần | Trọng số | Cách tính |
|---|---|---|
| **Interest Score** | 40% | Jaccard Similarity giữa tập sở thích của hai người |
| **ELO Score** | 35% | Điểm ELO / 3000 (max). ELO cập nhật sau mỗi swipe theo công thức chess ELO (K=32) |
| **Distance Score** | 15% | `max(0, 1 - km / maxDistanceKm)` — càng gần càng cao |
| **Activity Score** | 10% | Online = 1.0; Offline = `e^(-hoursAgo / 24)` |

**Công thức ELO update:**
```
expected  = 1 / (1 + 10^((fromElo - toElo) / 400))
newScore  = toElo + 32 × (actual - expected)
// Like → actual = 1.0 | Dislike → actual = 0.0
```

**Diversity Filter:** Tránh gợi ý liên tiếp ≥3 người cùng thành phố — tự động xen kẽ người từ thành phố khác.

---

## 🛠️ Công nghệ sử dụng

### Backend
| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 21 | Ngôn ngữ chính |
| Spring Boot | 4.0.3 | Framework backend |
| Spring Security | 6.x | Xác thực & phân quyền |
| Spring WebSocket | — | Realtime messaging (STOMP) |
| Spring Data JPA | — | ORM, tương tác database |
| JWT (jjwt) | 0.12.6 | Stateless authentication |
| Bucket4j | 8.10.1 | Rate limiting theo IP |
| Cloudinary SDK | 1.38.0 | Lưu trữ & xử lý ảnh |
| MySQL | 8.0 | Cơ sở dữ liệu |
| Lombok | 1.18.30 | Giảm boilerplate |
| Maven | 3.9 | Build & dependency |

### Frontend
| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| React | 19 | UI framework |
| Vite | 8.x | Build tool & dev server |
| Zustand | 5.x | State management |
| React Router | 7.x | Client-side routing |
| Socket.IO Client | 4.x | WebSocket client |
| Tailwind CSS | 3.x | Utility-first CSS |
| DaisyUI | 5.x | UI component library |
| Axios | 1.x | HTTP client |
| React Hot Toast | 2.x | Toast notifications |
| Lucide React | — | Icon library |

---

## 📁 Cấu trúc dự án

```
dating/
├── Dating/                                     # Spring Boot Backend
│   └── src/main/java/com/example/Dating/
│       ├── config/                             # Cấu hình hệ thống
│       │   ├── SecurityConfig.java             # Spring Security + JWT filter
│       │   ├── WebSocketSecurityConfig.java    # STOMP security + JWT validate
│       │   ├── AsyncConfig.java                # Thread pool cho async tasks
│       │   ├── WebConfig.java                  # CORS configuration
│       │   └── JacksonConfig.java              # JSON serialization
│       ├── controller/                         # REST & WebSocket controllers
│       ├── service/                            # Business logic (Interface + Impl)
│       ├── repository/                         # Spring Data JPA Repositories
│       ├── specification/                      # JPA Specifications (dynamic query)
│       ├── policy/
│       │   └── UnsendPolicy.java               # Tách biệt logic unsend message
│       ├── filter/
│       │   └── RateLimitFilter.java            # Bucket4j rate limiting per IP
│       ├── events/                             # Spring ApplicationEvent (async)
│       ├── dtos/                               # Request & Response DTOs
│       ├── entities/                           # JPA Entities
│       ├── mapper/                             # Entity <-> DTO Mappers
│       └── exception/                         # GlobalExceptionHandler
│
└── DatingFE/dating/                            # React Frontend
    └── src/
        ├── components/                         # Reusable UI components
        │   ├── match/                          # UserCard, MatchCard, MatchHelpers
        │   ├── skeletons/                      # Loading skeletons
        │   ├── ChatContainer.jsx
        │   ├── MessageInput.jsx
        │   ├── PostContainer.jsx
        │   └── Sidebar.jsx
        ├── page/                               # Các trang chính
        │   ├── LoginPage.jsx / SignupPage.jsx
        │   ├── HomePage.jsx
        │   ├── MatchPage.jsx
        │   └── ProfilePage.jsx
        ├── store/                              # Zustand stores
        └── lib/                               # Axios instance & utils
```

---

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api`

| Module | Method | Endpoint | Mô tả |
|---|---|---|---|
| **Auth** | POST | `/auth/register` | Đăng ký tài khoản |
| | POST | `/auth/login` | Đăng nhập → JWT |
| | POST | `/auth/refresh` | Làm mới Access Token |
| **Profile** | POST | `/profiles` | Tạo hồ sơ |
| | GET | `/profiles/{userId}` | Xem hồ sơ |
| | PUT | `/profiles/me` | Cập nhật hồ sơ |
| | GET | `/profiles/me/paginated` | Danh sách gợi ý (phân trang) |
| **Recommendation** | GET | `/recommendations/me` | Danh sách candidates đã scored |
| **Swipe** | POST | `/swipes` | Like / Dislike |
| **Match** | GET | `/matches/me` | Danh sách match |
| | DELETE | `/matches/{matchId}` | Unmatch |
| **Conversation** | GET | `/conversations/me` | Danh sách hội thoại |
| | POST | `/conversations` | Tạo hội thoại |
| **Message** | POST | `/messages/send` | Gửi tin nhắn text |
| | POST | `/messages/photo` | Gửi ảnh |
| | GET | `/messages/{conversationId}` | Lịch sử tin nhắn |
| | DELETE | `/messages/{id}/unsend` | Thu hồi cho cả hai |
| | DELETE | `/messages/{id}/delete-for-me` | Xóa phía mình |
| **Photo** | POST | `/photos` | Upload ảnh profile |
| | GET | `/photos/user/{userId}` | Ảnh của user |
| | DELETE | `/photos/{id}` | Xóa ảnh |
| **Presence** | POST | `/presence/me/online` | Set online |
| | POST | `/presence/me/offline` | Set offline |
| | GET | `/presence/{userId}` | Trạng thái của user |
| **Post** | GET | `/posts` | Feed bài viết |
| | POST | `/posts` | Tạo bài viết |
| | DELETE | `/posts/{postId}` | Xóa bài viết |
| **Interests** | GET | `/interests` | Danh sách sở thích |
| | POST | `/user-interests` | Thêm sở thích |
| | DELETE | `/user-interests/{id}` | Xóa sở thích |
| **Preference** | GET | `/preferences/me` | Xem preference |
| | PUT | `/preferences/me` | Cập nhật preference |

---

## 🚀 Cài đặt & Chạy dự án

### Yêu cầu
- Java 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Tài khoản [Cloudinary](https://cloudinary.com) (free tier đủ dùng)

### Backend

```bash
cd Dating

# 1. Tạo file cấu hình
cp src/main/resources/application-dev.properties.example \
   src/main/resources/application-dev.properties

# 2. Điền thông tin vào file (xem bảng bên dưới)

# 3. Chạy ứng dụng
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# Server: http://localhost:8080/api
```

#### Các biến cần cấu hình

| Biến | Ví dụ | Mô tả |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/dating` | JDBC URL |
| `spring.datasource.username` | `root` | MySQL username |
| `spring.datasource.password` | `yourpassword` | MySQL password |
| `jwt.secret` | `your-secret-≥32-chars` | JWT signing secret |
| `jwt.access-expiration` | `900000` | Access token TTL (ms) — 15 phút |
| `jwt.refresh-expiration` | `604800000` | Refresh token TTL (ms) — 7 ngày |
| `cloudinary.cloud_name` | `your-cloud` | Cloudinary cloud name |
| `cloudinary.api_key` | `123456789` | Cloudinary API key |
| `cloudinary.api_secret` | `your-secret` | Cloudinary API secret |
| `message.unsend.window-minutes` | `1440` | Cửa sổ thu hồi tin nhắn (phút) |

### Frontend

```bash
cd DatingFE/dating
npm install
npm run dev
# App: http://localhost:5173
```

---

## ⚡ Technical Highlights

- **ELO-based Recommendation**: Thuật toán gợi ý kết hợp 4 tín hiệu với Diversity Filter chống lặp khu vực địa lý.
- **WebSocket Security**: JWT validate ngay tại STOMP CONNECT — không một frame nào được xử lý trước khi xác thực.
- **UnsendPolicy Pattern**: Logic thu hồi tin nhắn tách hoàn toàn khỏi Service — thêm/sửa điều kiện chỉ cần sửa 1 class.
- **Bucket4j Rate Limiting**: Phân tầng 2 mức — 100 req/phút cho API thông thường, 10 req/phút cho auth — không cần Redis.
- **Batch Loading tránh N+1**: Recommendation engine load photos và interests cho toàn bộ pool bằng `findByUserIdIn()`.
- **JPA Specification**: Dynamic query cho recommendation filter — không hardcode SQL.
- **Async Event Bus**: Message unsend events xử lý bất đồng bộ qua Spring `ApplicationEventPublisher`.

---

## 👨‍💻 Tác giả

**Vũ Hoàng Chung**
- 📧 vuhoangchung2020@gmail.com
- GitHub: [@Hoangjunss](https://github.com/Hoangjunss)
