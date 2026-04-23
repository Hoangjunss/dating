package com.example.Dating.repository;

import com.example.Dating.entities.Notification;
import com.example.Dating.entities.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private UUID recipientId;

    @BeforeEach
    void setUp() {
        recipientId = UUID.randomUUID();
        Notification n1 = Notification.builder()
                .recipientUserId(recipientId)
                .type(NotificationType.NEW_MESSAGE)
                .title("New message")
                .body("Hello")
                .read(false)
                .build();
        Notification n2 = Notification.builder()
                .recipientUserId(recipientId)
                .type(NotificationType.NEW_MATCH)
                .title("Match")
                .body("You matched")
                .read(false)
                .build();
        entityManager.persist(n1);
        entityManager.persist(n2);
        entityManager.flush();
    }

    @Test
    void findByRecipientUserIdOrderByCreatedAtDesc_ShouldReturnNotifications() {
        var page = notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(recipientId, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void countByRecipientUserIdAndReadIsFalse_ShouldReturnTwo() {
        long count = notificationRepository.countByRecipientUserIdAndReadIsFalse(recipientId);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void markAllReadForRecipient_ShouldMarkAllAsRead() {
        int updated = notificationRepository.markAllReadForRecipient(recipientId);
        assertThat(updated).isEqualTo(2);
        // verify
        long unread = notificationRepository.countByRecipientUserIdAndReadIsFalse(recipientId);
        assertThat(unread).isZero();
    }
}