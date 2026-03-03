package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserPreferenceResponse {

    private UUID userId;
    private String genderPreference;
    private Integer minAge;
    private Integer maxAge;
    private Integer maxDistanceKm;
}