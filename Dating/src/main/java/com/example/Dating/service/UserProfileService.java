package com.example.Dating.service;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    /**
     * Creates a new user profile.
     */
    UserProfileResponse create(UserProfileCreateRequest request);

    /**
     * Retrieves a single profile by userId.
     */
    UserProfileResponse get(UUID userId);

    /**
     * Updates an existing profile.
     */
    UserProfileResponse update(UUID userId, UserProfileUpdateRequest request);

    /**
     * Retrieves all profiles.
     */
    List<UserProfileResponse> getAll();

    /**
     * Retrieves profiles with pagination support.
     */
    Page<UserProfileResponse> getAllPaginated(Pageable pageable);

    /**
     * Deletes a profile.
     */
    void delete(UUID userId);
}