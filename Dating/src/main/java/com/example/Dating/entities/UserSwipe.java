package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
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
    @Column(nullable = false)
    private UUID fromUserId;

    // target user
    @Column(nullable = false)
    private UUID toUserId;

    // true = like, false = pass
    private boolean isLiked;

    private Instant createdAt;
}