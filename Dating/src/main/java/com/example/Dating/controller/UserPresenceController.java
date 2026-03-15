package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for User Presence Management.
 * Provides endpoints for managing user online/offline status.
 */
@Slf4j
@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class UserPresenceController {

    private final UserPresenceService userPresenceService;

    /**
     * Sets a user's presence to online.
     * 
     * @param userId The user ID (UUID)
     * @return 200 OK status
     */
    @PostMapping("/{userId}/online")
    public ResponseEntity<Void> setOnline(
            @PathVariable UUID userId) {
        log.info("POST /api/presence/{}/online - Setting user online", userId);
        
        userPresenceService.setOnline(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Sets a user's presence to offline.
     * 
     * @param userId The user ID (UUID)
     * @return 200 OK status
     */
    @PostMapping("/{userId}/offline")
    public ResponseEntity<Void> setOffline(
            @PathVariable UUID userId) {
        log.info("POST /api/presence/{}/offline - Setting user offline", userId);
        
        userPresenceService.setOffline(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves user presence status.
     * 
     * @param userId The user ID (UUID)
     * @return User presence response with 200 OK status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserPresenceResponse> get(
            @PathVariable UUID userId) {
        log.info("GET /api/presence/{} - Fetching presence status", userId);
        
        UserPresenceResponse response = userPresenceService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes user presence data.
     * 
     * @param userId The user ID (UUID)
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId) {
        log.info("DELETE /api/presence/{} - Deleting presence", userId);
        
        userPresenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}