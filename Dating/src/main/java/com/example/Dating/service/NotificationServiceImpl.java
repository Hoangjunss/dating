package com.example.Dating.service;

import com.example.Dating.dtos.response.NotificationResponse;
import com.example.Dating.entities.Notification;
import com.example.Dating.entities.NotificationType;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;
    private final RealtimeNotificationService realtimeNotificationService;

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> listForUser(UUID recipientId, Pageable pageable) {
        return repository.findByRecipientUserIdOrderByCreatedAtDesc(recipientId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread(UUID recipientId) {
        return repository.countByRecipientUserIdAndReadIsFalse(recipientId);
    }

    @Override
    @Transactional
    public NotificationResponse createAndPush(
            UUID recipientId,
            NotificationType type,
            String title,
            String body,
            UUID conversationId,
            UUID relatedUserId
    ) {
        Notification entity = Notification.builder()
                .recipientUserId(recipientId)
                .type(type)
                .title(title)
                .body(body)
                .conversationId(conversationId)
                .relatedUserId(relatedUserId)
                .read(false)
                .build();

        entity = repository.save(entity);
        NotificationResponse dto = toResponse(entity);
        realtimeNotificationService.sendToUser(recipientId, dto);
        return dto;
    }

    @Override
    @Transactional
    public void markRead(UUID notificationId, UUID recipientId) {
        Notification n = repository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!n.getRecipientUserId().equals(recipientId)) {
            throw new ValidationException("Not your notification");
        }
        n.setRead(true);
        repository.save(n);
    }

    @Override
    @Transactional
    public void markAllRead(UUID recipientId) {
        repository.markAllReadForRecipient(recipientId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .conversationId(n.getConversationId())
                .relatedUserId(n.getRelatedUserId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
