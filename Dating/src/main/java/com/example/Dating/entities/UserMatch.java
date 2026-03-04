package com.example.Dating.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;
/**
 * Represents a successful match between two users.
 * A match is created when both users LIKE each other.
 * Only matched users are allowed to start conversations.
 */

@Entity
@Table(
        name = "user_matches",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userAId", "userBId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatch {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userAId;

    @Column(nullable = false)
    private UUID userBId;

    // allow unmatch / soft delete
    private Boolean active;

    private Instant matchedAt;

    @PrePersist
    void prePersist() {
        matchedAt = Instant.now();
        active = true;
    }
}