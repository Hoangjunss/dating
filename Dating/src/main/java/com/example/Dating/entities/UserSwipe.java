package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a swipe action between two users.
 * LIKE or PASS.
 */
@Entity
@Table(
        name = "user_swipes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fromUserId", "toUserId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSwipe {

    @Id
    @GeneratedValue
    private UUID id;

    // who swipes
    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private UserProfile fromUser;

    // target user
    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private UserProfile toUser;

    // true = like, false = pass
    private boolean isLiked;

    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}