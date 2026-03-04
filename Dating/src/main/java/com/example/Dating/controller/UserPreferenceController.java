package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.service.UserPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for User Preference Management.
 * Provides endpoints for managing user search and matching preferences.
 */
@Slf4j
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    /**
     * Creates or updates user preferences (upsert operation).
     * 
     * @param userId The user ID (UUID)
     * @param request Preference request
     * @return User preference response with 201 CREATED or 200 OK status
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> save(
            @PathVariable UUID userId,
            @Valid @RequestBody UserPreferenceRequest request) {
        log.info("PUT /api/preferences/{} - Saving preferences", userId);
        
        UserPreferenceResponse response = userPreferenceService.save(userId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Retrieves user preferences by userId.
     * 
     * @param userId The user ID (UUID)
     * @return User preference response with 200 OK status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserPreferenceResponse> get(
            @PathVariable UUID userId) {
        log.info("GET /api/preferences/{} - Fetching preferences", userId);
        
        UserPreferenceResponse response = userPreferenceService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes user preferences.
     * 
     * @param userId The user ID (UUID)
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId) {
        log.info("DELETE /api/preferences/{} - Deleting preferences", userId);
        
        userPreferenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}