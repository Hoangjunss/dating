package com.example.Dating.mapper;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.entities.UserPreference;

import java.util.UUID;

public final class UserPreferenceMapper {

    private UserPreferenceMapper() {}

    public static UserPreference toEntity(UUID userId, UserPreferenceRequest r) {
        return UserPreference.builder()
                .userId(userId)
                .genderPreference(r.getGenderPreference())
                .minAge(r.getMinAge())
                .maxAge(r.getMaxAge())
                .maxDistanceKm(r.getMaxDistanceKm())
                .build();
    }

    public static UserPreferenceResponse toResponse(UserPreference e) {
        return UserPreferenceResponse.builder()
                .userId(e.getUserId())
                .genderPreference(e.getGenderPreference())
                .minAge(e.getMinAge())
                .maxAge(e.getMaxAge())
                .maxDistanceKm(e.getMaxDistanceKm())
                .build();
    }

    public static void update(UserPreference e, UserPreferenceRequest r) {
        if (r.getGenderPreference() != null) e.setGenderPreference(r.getGenderPreference());
        if (r.getMinAge() != null) e.setMinAge(r.getMinAge());
        if (r.getMaxAge() != null) e.setMaxAge(r.getMaxAge());
        if (r.getMaxDistanceKm() != null) e.setMaxDistanceKm(r.getMaxDistanceKm());
    }
}