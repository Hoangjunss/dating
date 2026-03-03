package com.example.Dating.dtos.request;

import lombok.Data;

@Data
public class UserPreferenceRequest {

    private String genderPreference;
    private Integer minAge;
    private Integer maxAge;
    private Integer maxDistanceKm;
}