package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Mapping table between user and interest.
 * Each row means a user selects one interest.
 */
@Entity
@Table(
        name = "user_interests",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"userId", "interestId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInterest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;
}