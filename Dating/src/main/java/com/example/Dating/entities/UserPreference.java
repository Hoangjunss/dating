package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Stores matching filters configured by user.
 * Used when searching/swiping candidates.
 */
@Entity
@Table(name = "user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    // gender they want to match with
    private String genderPreference;

    // age filter
    private Integer minAge;
    private Integer maxAge;

    // search radius (km)
    private Integer maxDistanceKm;
}