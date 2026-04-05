package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class UserPresenceController {

    private final UserPresenceService userPresenceService;

    /**
     * POST /api/presence/me/online
     * User chỉ có thể set online/offline cho chính mình
     */
    @PostMapping("/me/online")
    public ResponseEntity<Void> setOnline(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("POST /api/presence/me/online - userId: {}", userId);
        userPresenceService.setOnline(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /api/presence/me/offline
     */
    @PostMapping("/me/offline")
    public ResponseEntity<Void> setOffline(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("POST /api/presence/me/offline - userId: {}", userId);
        userPresenceService.setOffline(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/presence/{userId}
     * Lấy trạng thái online của bất kỳ user nào (đã authenticated).
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserPresenceResponse> get(@PathVariable UUID userId) {
        log.info("GET /api/presence/{} - Fetching presence", userId);
        UserPresenceResponse response = userPresenceService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/presence/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/presence/me - userId: {}", userId);
        userPresenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
