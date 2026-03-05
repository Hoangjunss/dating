package com.example.Dating.service;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;

import java.util.UUID;

public interface UserPreferenceService {

    /**
     * Creates or updates user preferences.
     */
    UserPreferenceResponse save(UUID userId, UserPreferenceRequest request);

    /**
     * Retrieves user preferences.
     */
    UserPreferenceResponse get(UUID userId);

    /**
     * Deletes user preferences.
     */
    void delete(UUID userId);
}