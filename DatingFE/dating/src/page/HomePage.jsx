import { useChatStore } from "../store/useChatStore";

import Sidebar from "../components/Sidebar";
import NoChatSelected from "../components/NoChatSelected";
import ChatContainer from "../components/ChatContainer";

const HomePage = () => {
  const { selectedUser } = useChatStore();

  return (
    // Đổi màu nền sang hồng cực dịu, thêm padding để tạo hiệu ứng Bento
    <div className="min-h-screen bg-[#FFF5F7] pt-20 pb-6 px-4 md:px-6Selection:bg-rose-200">
      <div className="max-w-[1700px] mx-auto h-[calc(100vh-8rem)]">
        {/* Bố cục Flex, thêm gap và đổ bóng nhẹ */}
        <div className="flex gap-5 h-full">
          
          {/* Sidebar - Bo góc lớn, đổ bóng, hiệu ứng kính mờ nhẹ */}
          <div className="w-20 md:w-80 h-full flex-shrink-0 bg-white/60 backdrop-blur-xl rounded-[2.5rem] shadow-xl shadow-rose-100/50 border border-white/50 overflow-hidden transition-all duration-300">
            <Sidebar />
          </div>

          {/* Chat Panel - Bo góc lớn, đổ bóng đậm hơn để tạo chiều sâu */}
          <div className="flex-1 h-full bg-white rounded-[2.5rem] shadow-2xl shadow-rose-100 border border-rose-50 overflow-hidden transition-all duration-300">
            {!selectedUser ? <NoChatSelected /> : <ChatContainer />}
          </div>
        </div>
      </div>
    </div>
  );
};

export default HomePage;