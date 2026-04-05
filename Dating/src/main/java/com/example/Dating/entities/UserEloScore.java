package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores Elo-like attractiveness score for each user.
 * Updated every time a swipe action happens.
 *
 * Score range: 0–3000 (default 1400, similar to chess Elo starting point).
 * Like from high-Elo user  → bigger boost.
 * Pass (explicit dislike) → small penalty.
 */
@Entity
@Table(name = "user_elo_scores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEloScore {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    @Builder.Default
    private Double score = 1400.0;

    /** Total number of times this user was shown to others. */
    @Builder.Default
    private Long totalSeen = 0L;

    /** Total number of likes received. */
    @Builder.Default
    private Long totalLikes = 0L;

    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    /** Like-rate convenience getter (avoid division by zero). */
    public double getLikeRate() {
        if (totalSeen == 0) return 0.5; // cold-start: assume 50%
        return (double) totalLikes / totalSeen;
    }
}