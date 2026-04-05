package com.example.Dating.dtos.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

/**
 * A profile returned by the recommendation engine.
 * Extends basic profile info with a score breakdown for transparency/debug.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {

    private UUID userId;
    private String displayName;
    private String gender;
    private Integer age;
    private String city;
    private Double distanceKm;
    private String bio;
    private List<String> photoUrls;
    private List<String> interests;

    /** Composite recommendation score (higher = shown first). Not exposed to end-user UI. */
    private Double score;

    /** Individual score components — useful for A/B testing. */
    private Double interestScore;
    private Double eloScore;
    private Double distanceScore;
    private Double activityScore;
}

