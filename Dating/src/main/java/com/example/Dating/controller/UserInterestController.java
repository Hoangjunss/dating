package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.service.UserInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User Interest Management.
 * Provides endpoints for managing user interests and hobbies.
 */
@Slf4j
@RestController
@RequestMapping("/api/user-interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    /**
     * Adds an interest to a user.
     * 
     * @param request User interest request
     * @return Created user interest response with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<UserInterestResponse> create(
            @Valid @RequestBody UserInterestRequest request) {
        log.info("POST /api/user-interests - Adding interest {} to user {}", 
                request.getInterestId(), request.getUserId());
        
        UserInterestResponse response = userInterestService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all interests for a specific user.
     * 
     * @param userId The user ID (UUID)
     * @return List of user's interests
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInterestResponse>> getByUser(
            @PathVariable UUID userId) {
        log.info("GET /api/user-interests/{} - Fetching interests for user", userId);
        
        List<UserInterestResponse> responses = userInterestService.getByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Removes an interest from a user.
     * 
     * @param userId The user ID (UUID) - path variable
     * @param interestId The interest ID (UUID) - path variable
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{userId}/{interestId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId,
            @PathVariable UUID interestId) {
        log.info("DELETE /api/user-interests/{}/{} - Removing interest from user", userId, interestId);
        
        userInterestService.delete(userId, interestId);
        return ResponseEntity.noContent().build();
    }
}