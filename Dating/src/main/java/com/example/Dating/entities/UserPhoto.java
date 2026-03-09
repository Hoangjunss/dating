package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Stores user's uploaded photos.
 * Used for profile gallery display and avatar.
 */
@Entity
@Table(name = "user_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPhoto {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile", nullable = false)
    private UserProfile userProfile;

    @Column(nullable = false)
    private String url;

    // display order (1 = avatar)
    private Integer sortOrder;

    private boolean isPrimary;

    private Instant createdAt;
}