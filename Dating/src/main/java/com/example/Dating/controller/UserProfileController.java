package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User Profile Management.
 * Provides endpoints for CRUD operations on user profiles.
 */
@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Creates a new user profile.
     * 
     * @param request User profile creation request
     * @return Created user profile with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<UserProfileResponse> create(
            @Valid @RequestBody UserProfileCreateRequest request) {
        log.info("POST /api/profiles - Creating user profile");
        
        UserProfileResponse response = userProfileService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves a user profile by ID.
     * 
     * @param userId The user ID (UUID)
     * @return User profile with 200 OK status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> get(
            @PathVariable UUID userId) {
        log.info("GET /api/profiles/{} - Fetching user profile", userId);
        
        UserProfileResponse response = userProfileService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all user profiles (non-paginated).
     * 
     * @return List of all user profiles
     */
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAll() {
        log.info("GET /api/profiles - Fetching all user profiles");
        
        List<UserProfileResponse> responses = userProfileService.getAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves all user profiles with pagination support.
     * 
     * @param pageable Pagination parameters (page, size, sort)
     * @return Paginated list of user profiles
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<UserProfileResponse>> getAllPaginated(
            Pageable pageable) {
        log.info("GET /api/profiles/paginated - Fetching paginated profiles: {}", pageable);
        
        Page<UserProfileResponse> responses = userProfileService.getAllPaginated(pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * Updates a user profile.
     * Only non-null fields will be updated.
     * 
     * @param userId The user ID (UUID)
     * @param request Profile update request
     * @return Updated user profile with 200 OK status
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        log.info("PUT /api/profiles/{} - Updating user profile", userId);
        
        UserProfileResponse response = userProfileService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a user profile.
     * 
     * @param userId The user ID (UUID)
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId) {
        log.info("DELETE /api/profiles/{} - Deleting user profile", userId);
        
        userProfileService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}