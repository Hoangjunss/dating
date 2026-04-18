package com.example.Dating.service;

import com.example.Dating.dtos.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Đẩy thông báo tới từng user qua STOMP user destination:
 * {@code /user/queue/notifications} — client subscribe cùng đường dẫn.
 * {@code userName} phải trùng {@code Principal#getName()} (UUID string) như lúc CONNECT WebSocket.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealtimeNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(UUID userId, NotificationResponse payload) {
        if (userId == null || payload == null) {
            return;
        }
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    payload
            );
        } catch (Exception e) {
            log.warn("Failed to push notification to user {}: {}", userId, e.getMessage());
        }
    }
}
