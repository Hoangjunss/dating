package com.example.Dating.service;

import com.example.Dating.dtos.response.NotificationResponse;
import com.example.Dating.entities.Notification;
import com.example.Dating.entities.NotificationType;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.NotificationRepository;
import com.example.Dating.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository repository;
    @Mock private RealtimeNotificationService realtimeNotificationService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private UUID recipientId;
    private UUID notificationId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        recipientId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        notification = Notification.builder()
                .id(notificationId)
                .recipientUserId(recipientId)
                .type(NotificationType.NEW_MESSAGE)
                .title("New message")
                .body("Hello")
                .read(false)
                .build();
    }

    @Test
    void listForUser_ShouldReturnPage() {
        when(repository.findByRecipientUserIdOrderByCreatedAtDesc(eq(recipientId), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(notification)));

        var page = notificationService.listForUser(recipientId, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void countUnread_ShouldReturnCount() {
        when(repository.countByRecipientUserIdAndReadIsFalse(recipientId)).thenReturn(5L);
        long count = notificationService.countUnread(recipientId);
        assertThat(count).isEqualTo(5L);
    }

    @Test
    void createAndPush_ShouldSaveAndSend() {
        when(repository.save(any(Notification.class))).thenReturn(notification);
        NotificationResponse response = notificationService.createAndPush(
                recipientId, NotificationType.NEW_MATCH, "Match!", "You matched", UUID.randomUUID(), UUID.randomUUID());
        assertThat(response.getId()).isEqualTo(notificationId);
        verify(realtimeNotificationService).sendToUser(eq(recipientId), any());
    }

    @Test
    void markRead_Success() {
        when(repository.findById(notificationId)).thenReturn(Optional.of(notification));
        notificationService.markRead(notificationId, recipientId);
        assertThat(notification.isRead()).isTrue();
        verify(repository).save(notification);
    }

    @Test
    void markRead_NotOwner_Throws() {
        UUID differentUser = UUID.randomUUID();
        when(repository.findById(notificationId)).thenReturn(Optional.of(notification));
        assertThatThrownBy(() -> notificationService.markRead(notificationId, differentUser))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void markAllRead_Success() {
        when(repository.markAllReadForRecipient(recipientId)).thenReturn(3);
        notificationService.markAllRead(recipientId);
        verify(repository).markAllReadForRecipient(recipientId);
    }
}