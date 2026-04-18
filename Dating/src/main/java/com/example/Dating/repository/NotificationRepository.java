package com.example.Dating.repository;

import com.example.Dating.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId, Pageable pageable);

    long countByRecipientUserIdAndReadIsFalse(UUID recipientUserId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipientUserId = :recipientId AND n.read = false")
    int markAllReadForRecipient(@Param("recipientId") UUID recipientId);
}
