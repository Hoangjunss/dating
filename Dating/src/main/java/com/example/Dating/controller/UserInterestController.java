package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.service.UserInterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
     * POST /api/user-interests
     * Body: { "interestId": "uuid" }
     * Adds an interest to a user.
     * 
     * @param request User interest request
     * @return Created user interest response with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<UserInterestResponse> create(
            @Valid @RequestBody UserInterestRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        request.setUserId(userId);   // Override từ JWT

        log.info("POST /api/user-interests - userId: {}, interestId: {}",
                userId, request.getInterestId());

        UserInterestResponse response = userInterestService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all interests for a specific user.
     * 
     * @param userId The user ID (UUID)
     * @return List of user's interests
     * GET /api/user-interests/{userId}
     * Lấy interests của user khác (public info, để hiển thị profile).
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserInterestResponse>> getByUser(@PathVariable UUID userId) {
        log.info("GET /api/user-interests/{} - Fetching interests", userId);
        List<UserInterestResponse> responses = userInterestService.getByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/user-interests/me
     * Lấy danh sách interests của current user.
     *
     */
    @GetMapping("/me")
    public ResponseEntity<List<UserInterestResponse>> getMyInterests(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/user-interests/me - userId: {}", userId);

        List<UserInterestResponse> responses = userInterestService.getByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Removes an interest from a user.
     *  DELETE /api/user-interests/{interestId}
     * userId lấy từ JWT
     * @param interestId The interest ID (UUID) - path variable
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{interestId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID interestId,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/user-interests/{} - userId: {}", interestId, userId);

        userInterestService.delete(userId, interestId);
        return ResponseEntity.noContent().build();
    }
}