package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a chat message inside a conversation.
 *
 * Thu hồi tin nhắn:
 *  - unsent = true  → "Unsend for everyone": content ẩn với tất cả
 *  - "Delete for me" được lưu riêng ở bảng MessageDeletion
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserProfile sender;

    @Column(nullable = false, length = 2000)
    private String content;

    @Builder.Default
    private Boolean seen = false;

    private Instant sentAt;

    /**
     * true = tin nhắn đã bị unsend cho tất cả mọi người.
     * Content vẫn giữ trong DB để audit, nhưng API sẽ trả null.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean unsent = false;

    @PrePersist
    void prePersist() {
        sentAt = Instant.now();
        if (seen == null)   seen   = false;
        if (unsent == null) unsent = false;
    }
}