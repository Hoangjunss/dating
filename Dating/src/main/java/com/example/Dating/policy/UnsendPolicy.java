package com.example.Dating.policy;

import com.example.Dating.entities.Message;
import com.example.Dating.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * Tập trung toàn bộ điều kiện cho phép "Unsend for everyone".
 *
 * Service chỉ gọi policy.validate(message) — không chứa bất kỳ điều kiện nào trực tiếp trong service logic.
 *
 * Lợi ích: muốn thêm/bớt/sửa điều kiện → chỉ sửa class này, không động vào MessageServiceImpl.
 *
 * Cấu hình trong application.properties:
 *   message.unsend.window-minutes=1440   # 1440 phút = 24 giờ
 *   message.unsend.window-minutes=0      # 0 = không giới hạn thời gian
 */
@Slf4j
@Component
public class UnsendPolicy {

    @Value("${message.unsend.window-minutes:1440}")
    private long windowMinutes;

    /**
     * Kiểm tra xem một tin nhắn có thể bị unsend hay không.
     * Ném ValidationException kèm lý do cụ thể nếu không hợp lệ.
     *
     * @param message tin nhắn cần kiểm tra
     */
    public void validate(Message message) {

        if (Boolean.TRUE.equals(message.getUnsent())) {
            throw new ValidationException("Message has already been unsent");
        }

        if (windowMinutes > 0) {
            Instant deadline = message.getSentAt().plus(Duration.ofMinutes(windowMinutes));
            if (Instant.now().isAfter(deadline)) {
                log.debug("Unsend rejected - messageId: {}, sentAt: {}, deadline: {}",
                        message.getId(), message.getSentAt(), deadline);
                throw new ValidationException(
                        "Cannot unsend this message — the " + humanReadableWindow() + " time limit has passed");
            }
        }

        // --- Thêm điều kiện mới tại đây về sau ---
        // Ví dụ: if (Boolean.TRUE.equals(message.getSeen())) { throw ... }
    }

    /**
     * Trả về window dạng đọc được để dùng trong error message.
     * VD: 60 → "1 hour", 1440 → "24 hours", 30 → "30 minutes"
     */
    private String humanReadableWindow() {
        if (windowMinutes % 60 == 0) {
            long hours = windowMinutes / 60;
            return hours + (hours == 1 ? " hour" : " hours");
        }
        return windowMinutes + (windowMinutes == 1 ? " minute" : " minutes");
    }

    /** Dùng để log hoặc test — expose window hiện tại. */
    public long getWindowMinutes() {
        return windowMinutes;
    }
}