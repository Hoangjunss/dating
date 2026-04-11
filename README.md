# 💘 Dating App — Real-time Dating Platform

<div align="right">
  <a href="./README.vi.md">🇻🇳 Tiếng Việt</a>
</div>

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-19-blue?logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-green)
![JWT](https://img.shields.io/badge/JWT-0.12.6-black)

A full-stack dating platform featuring a smart recommendation engine based on **ELO Score + Jaccard Similarity**, real-time messaging via **WebSocket/STOMP**, and multi-layer security with **JWT + Rate Limiting**.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Screenshots](#screenshots)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Recommendation Algorithm](#recommendation-algorithm)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)

---

## 🌟 Overview

Dating App is a full-stack web application built on a **Client-Server** model:

- **Backend**: RESTful API with Spring Boot 4, handling all business logic, matching algorithm, security, and real-time messaging.
- **Frontend**: Single Page Application with React 19 + Zustand + DaisyUI, delivering a smooth swipe and chat experience.

What sets this apart from typical dating apps: the system doesn't just filter by preference — it ranks candidates through a **composite scoring algorithm** (ELO + shared interests + distance + recent activity), ensuring recommendations are always diverse and highly relevant.

---

## ✨ Features

### 🔐 Authentication & Security
- Register / Login with email + password (BCrypt hashing).
- **Stateless JWT**: Access Token (short-lived) + Refresh Token — auto-renew on expiry.
- **WebSocket Security**: JWT validated at STOMP CONNECT header — anonymous connections rejected immediately.
- **Tiered Rate Limiting** (Bucket4j) per IP:
  - Standard API: 100 req/min/IP
  - Auth endpoints (`/api/auth/**`): 10 req/min/IP — brute-force protection

### 👤 User Profile
- Create and update profile (display name, gender, birthdate, city, bio, GPS coordinates).
- Upload multiple photos with sort order — stored on **Cloudinary**.
- Manage personal interest tags.
- Set search preferences: target gender, age range (min/max), max distance (km).

### 💡 Smart Recommendation & Matching
- **Composite scoring** with 4 independent signals (see [algorithm details](#recommendation-algorithm)).
- **ELO Score** updated dynamically after every swipe.
- **Diversity Filter**: prevents consecutive suggestions from the same city.
- Fallback candidates when the filtered pool drops below 10 people.
- **Swipe** mechanic (Like / Dislike) — automatically creates a Match + Conversation on mutual like.

### 💬 Real-time Messaging
- 1-on-1 chat via **WebSocket/STOMP** — latency under 50ms (local, 50 concurrent connections).
- **Photo messages** (Cloudinary upload).
- **Unsend for everyone** — configurable time window via `UnsendPolicy` (default 24 hours).
- **Delete for me** — removes only from sender's view.
- Real-time **Online/Offline** presence tracking.

### 📝 Community Posts
- Create, view, and delete posts with images.
- Paginated post feed.

### 🔌 WebSocket Channels
| Channel | Direction | Description |
|---|---|---|
| `/ws` | Connect | STOMP connection endpoint |
| `/app/chat.send` | Client → Server | Send text message |
| `/app/chat.photo` | Client → Server | Send photo |
| `/topic/messages/{conversationId}` | Server → Client | Receive messages |
| `/user/{userId}/queue/notifications` | Server → Client | New match notifications |

---

## 🏗️ System Architecture

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
       │  (Port 3306)│           │  (Media CDN) │
       └─────────────┘           └──────────────┘
```

---

## 🧠 Recommendation Algorithm

Each candidate is scored across **4 independent signals**, combined into a composite score:

```
compositeScore = 0.40 × interestScore
              + 0.35 × eloScore
              + 0.15 × distanceScore
              + 0.10 × activityScore
              + rand(0, 0.05)     ← small randomness factor
```

| Signal | Weight | Calculation |
|---|---|---|
| **Interest Score** | 40% | Jaccard Similarity between the two users' interest sets |
| **ELO Score** | 35% | `eloScore / 3000` (max). ELO updated after each swipe using chess ELO formula (K=32) |
| **Distance Score** | 15% | `max(0, 1 - km / maxDistanceKm)` — closer = higher |
| **Activity Score** | 10% | Online = 1.0; Offline = `e^(-hoursAgo / 24)` |

**ELO update formula:**
```
expected  = 1 / (1 + 10^((fromElo - toElo) / 400))
newScore  = toElo + 32 × (actual - expected)
// Like → actual = 1.0 | Dislike → actual = 0.0
```

**Diversity Filter:** Prevents ≥3 consecutive candidates from the same city — automatically interleaves people from other cities.

---

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Primary language |
| Spring Boot | 4.0.3 | Backend framework |
| Spring Security | 6.x | Auth & authorization |
| Spring WebSocket | — | Real-time messaging (STOMP) |
| Spring Data JPA | — | ORM & database access |
| JWT (jjwt) | 0.12.6 | Stateless authentication |
| Bucket4j | 8.10.1 | IP-based rate limiting |
| Cloudinary SDK | 1.38.0 | Image storage & processing |
| MySQL | 8.0 | Primary database |
| Lombok | 1.18.30 | Boilerplate reduction |
| Maven | 3.9 | Build & dependency management |

### Frontend
| Technology | Version | Purpose |
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

## 📁 Project Structure

```
dating/
├── Dating/                                     # Spring Boot Backend
│   └── src/main/java/com/example/Dating/
│       ├── config/                             # App configuration
│       │   ├── SecurityConfig.java             # Spring Security + JWT filter
│       │   ├── WebSocketSecurityConfig.java    # STOMP security + JWT validation
│       │   ├── AsyncConfig.java                # Thread pool for async tasks
│       │   ├── WebConfig.java                  # CORS configuration
│       │   └── JacksonConfig.java              # JSON serialization
│       ├── controller/                         # REST & WebSocket controllers
│       ├── service/                            # Business logic (Interface + Impl)
│       ├── repository/                         # Spring Data JPA Repositories
│       ├── specification/                      # JPA Specifications (dynamic queries)
│       ├── policy/
│       │   └── UnsendPolicy.java               # Isolated unsend message logic
│       ├── filter/
│       │   └── RateLimitFilter.java            # Bucket4j per-IP rate limiting
│       ├── events/                             # Async Spring ApplicationEvents
│       ├── dtos/                               # Request & Response DTOs
│       ├── entities/                           # JPA Entities
│       ├── mapper/                             # Entity <-> DTO Mappers
│       └── exception/                         # Global exception handler
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
        ├── page/                               # Route-level pages
        │   ├── LoginPage.jsx / SignupPage.jsx
        │   ├── HomePage.jsx
        │   ├── MatchPage.jsx
        │   └── ProfilePage.jsx
        ├── store/                              # Zustand stores
        └── lib/                               # Axios instance & utilities
```

---

## 🔌 API Endpoints

Base URL: `http://localhost:8080/api`

| Module | Method | Endpoint | Description |
|---|---|---|---|
| **Auth** | POST | `/auth/register` | Register account |
| | POST | `/auth/login` | Login → JWT |
| | POST | `/auth/refresh` | Refresh access token |
| **Profile** | POST | `/profiles` | Create profile |
| | GET | `/profiles/{userId}` | Get profile |
| | PUT | `/profiles/me` | Update profile |
| | GET | `/profiles/me/paginated` | Paginated suggestion list |
| **Recommendation** | GET | `/recommendations/me` | Scored candidate list |
| **Swipe** | POST | `/swipes` | Like / Dislike |
| **Match** | GET | `/matches/me` | Match list |
| | DELETE | `/matches/{matchId}` | Unmatch |
| **Conversation** | GET | `/conversations/me` | Conversation list |
| | POST | `/conversations` | Create conversation |
| **Message** | POST | `/messages/send` | Send text message |
| | POST | `/messages/photo` | Send photo |
| | GET | `/messages/{conversationId}` | Message history |
| | DELETE | `/messages/{id}/unsend` | Unsend for everyone |
| | DELETE | `/messages/{id}/delete-for-me` | Delete for me |
| **Photo** | POST | `/photos` | Upload profile photo |
| | GET | `/photos/user/{userId}` | Get user photos |
| | DELETE | `/photos/{id}` | Delete photo |
| **Presence** | POST | `/presence/me/online` | Set online |
| | POST | `/presence/me/offline` | Set offline |
| | GET | `/presence/{userId}` | Get presence status |
| **Post** | GET | `/posts` | Post feed |
| | POST | `/posts` | Create post |
| | DELETE | `/posts/{postId}` | Delete post |
| **Interests** | GET | `/interests` | All interests |
| | POST | `/user-interests` | Add interest |
| | DELETE | `/user-interests/{id}` | Remove interest |
| **Preference** | GET | `/preferences/me` | Get preference |
| | PUT | `/preferences/me` | Update preference |

---

## 🚀 Getting Started

### Requirements
- Java 21+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- [Cloudinary](https://cloudinary.com) account (free tier is sufficient)

### Backend

```bash
cd Dating

# 1. Create config file
cp src/main/resources/application-dev.properties.example \
   src/main/resources/application-dev.properties

# 2. Fill in your values (see table below)

# 3. Run
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# Server: http://localhost:8080/api
```

#### Environment Variables

| Key | Example | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/dating` | JDBC URL |
| `spring.datasource.username` | `root` | MySQL username |
| `spring.datasource.password` | `yourpassword` | MySQL password |
| `jwt.secret` | `your-secret-≥32-chars` | JWT signing secret |
| `jwt.access-expiration` | `900000` | Access token TTL (ms) |
| `jwt.refresh-expiration` | `604800000` | Refresh token TTL (ms) |
| `cloudinary.cloud_name` | `your-cloud` | Cloudinary cloud name |
| `cloudinary.api_key` | `123456789` | Cloudinary API key |
| `cloudinary.api_secret` | `your-secret` | Cloudinary API secret |
| `message.unsend.window-minutes` | `1440` | Unsend time window (minutes) |

### Frontend

```bash
cd DatingFE/dating
npm install
npm run dev
# App: http://localhost:5173
```

---

## ⚡ Technical Highlights

- **ELO-based Recommendation**: 4-signal composite scoring (shared interests, ELO, distance, activity) with a Diversity Filter to prevent geographic clustering.
- **WebSocket Security**: JWT validated at STOMP CONNECT — no frames processed before authentication.
- **UnsendPolicy Pattern**: Unsend logic fully decoupled from the Service layer — change rules by editing one class only.
- **Tiered Rate Limiting**: 100 req/min for standard API, 10 req/min for auth — in-memory per IP via Bucket4j, no Redis needed.
- **Batch Loading (no N+1)**: Recommendation engine loads photos and interests for the entire candidate pool via `findByUserIdIn()`.
- **JPA Specification**: Dynamic recommendation filtering (gender, age, distance, excluded IDs) — no hardcoded SQL.
- **Async Event Bus**: Unsend events processed asynchronously via Spring `ApplicationEventPublisher` — WebSocket thread never blocked.

---

## 👨‍💻 Author

**Vu Hoang Chung**
- 📧 vuhoangchung2020@gmail.com
- GitHub: [@Hoangjunss](https://github.com/Hoangjunss)
