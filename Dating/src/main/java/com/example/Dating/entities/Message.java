package com.example.Dating.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a chat message inside a conversation.
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
    @JoinColumn(name = "conversation_id", nullable = false)
    private UserProfile sender;

    @Column(nullable = false, length = 2000)
    private String content;

    private Boolean seen;

    private Instant sentAt;

    @PrePersist
    void prePersist() {
        sentAt = Instant.now();
        seen = false;
    }
}