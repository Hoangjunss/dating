// ============================================================
//  mockData.js  –  Dữ liệu mẫu cho MatchPage
//  TODO: Xoá file này và thay bằng call API thật
// ============================================================

export const MOCK_CURRENT_USER = {
  userId: "user_000",
  fullName: "Nguyễn Văn A",
  email: "nguyenvana@example.com",
  profilePic: "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix",
  createdAt: "2024-01-15T08:00:00.000Z",
};

export const MOCK_USERS = [
  {
    userId: "user_001",
    _id: "user_001",
    fullName: "Trần Thị Bích Ngọc",
    email: "bichhngoc@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Ngoc",
    location: "Hồ Chí Minh",
    createdAt: "2024-03-10T09:30:00.000Z",
    bio: "Yêu du lịch, cafe và âm nhạc 🎵",
    interests: ["Du lịch", "Café", "Âm nhạc"],
  },
  {
    userId: "user_002",
    _id: "user_002",
    fullName: "Lê Minh Tuấn",
    email: "minhtuan@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Tuan",
    location: "Hà Nội",
    createdAt: "2024-02-20T14:00:00.000Z",
    bio: "Lập trình viên, mê game và đọc sách 📚",
    interests: ["Lập trình", "Gaming", "Sách"],
  },
  {
    userId: "user_003",
    _id: "user_003",
    fullName: "Phạm Thị Lan Anh",
    email: "lananh@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=LanAnh",
    location: "Đà Nẵng",
    createdAt: "2024-04-05T11:15:00.000Z",
    bio: "Nhiếp ảnh, yoga và ẩm thực 🍜",
    interests: ["Nhiếp ảnh", "Yoga", "Ẩm thực"],
  },
  {
    userId: "user_004",
    _id: "user_004",
    fullName: "Nguyễn Hữu Đức",
    email: "huuduc@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Duc",
    location: "Cần Thơ",
    createdAt: "2024-01-28T07:45:00.000Z",
    bio: "Thích thể thao và nấu ăn 🍳",
    interests: ["Bóng đá", "Gym", "Nấu ăn"],
  },
  {
    userId: "user_005",
    _id: "user_005",
    fullName: "Võ Thị Thanh Hương",
    email: "thanhuong@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Huong",
    location: "Hồ Chí Minh",
    createdAt: "2024-05-01T16:00:00.000Z",
    bio: "Designer, mê phim và nghệ thuật 🎨",
    interests: ["Thiết kế", "Phim", "Nghệ thuật"],
  },
  {
    userId: "user_006",
    _id: "user_006",
    fullName: "Đặng Quốc Bảo",
    email: "quocbao@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Bao",
    location: "Hà Nội",
    createdAt: "2024-03-22T10:30:00.000Z",
    bio: "Kỹ sư, yêu thiên nhiên và leo núi 🏔️",
    interests: ["Leo núi", "Thiên nhiên", "Cắm trại"],
  },
  {
    userId: "user_007",
    _id: "user_007",
    fullName: "Huỳnh Thị Mỹ Linh",
    email: "mylinh@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Linh",
    location: "Nha Trang",
    createdAt: "2024-02-14T08:00:00.000Z",
    bio: "Giáo viên, thích đọc sách và nấu ăn 📖",
    interests: ["Giáo dục", "Sách", "Nấu ăn"],
  },
  {
    userId: "user_008",
    _id: "user_008",
    fullName: "Bùi Tiến Dũng",
    email: "tiendung@example.com",
    profilePic: "https://api.dicebear.com/7.x/personas/svg?seed=Dung",
    location: "Hải Phòng",
    createdAt: "2024-04-18T13:00:00.000Z",
    bio: "Bác sĩ, chạy bộ và du lịch bụi 🏃",
    interests: ["Y học", "Chạy bộ", "Du lịch"],
  },
];

// Online user IDs (giả lập)
export const MOCK_ONLINE_USERS = ["user_001", "user_003", "user_005", "user_007"];
