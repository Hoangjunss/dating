package com.example.Dating.service;

import com.example.Dating.dtos.response.NotificationResponse;
import com.example.Dating.entities.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    Page<NotificationResponse> listForUser(UUID recipientId, Pageable pageable);

    long countUnread(UUID recipientId);

    NotificationResponse createAndPush(
            UUID recipientId,
            NotificationType type,
            String title,
            String body,
            UUID conversationId,
            UUID relatedUserId
    );

    void markRead(UUID notificationId, UUID recipientId);

    void markAllRead(UUID recipientId);
}
