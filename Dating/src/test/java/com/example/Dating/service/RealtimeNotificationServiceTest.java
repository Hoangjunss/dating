package com.example.Dating.service;

import com.example.Dating.dtos.response.NotificationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealtimeNotificationServiceTest {

    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private RealtimeNotificationService realtimeNotificationService;

    private UUID userId;
    private NotificationResponse notification;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notification = NotificationResponse.builder()
                .id(UUID.randomUUID())
                .title("Test")
                .body("Body")
                .build();
    }

    @Test
    void sendToUser_ShouldSendToCorrectDestination() {
        realtimeNotificationService.sendToUser(userId, notification);
        verify(messagingTemplate).convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    @Test
    void sendToUser_NullUserId_ShouldNotSend() {
        realtimeNotificationService.sendToUser(null, notification);
        verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
    }

    @Test
    void sendToUser_NullPayload_ShouldNotSend() {
        realtimeNotificationService.sendToUser(userId, null);
        verify(messagingTemplate, never()).convertAndSendToUser(any(), any(), any());
    }
}