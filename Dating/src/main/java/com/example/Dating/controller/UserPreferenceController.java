package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.service.UserPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    /**
     * PUT /api/preferences/me
     * Tạo hoặc cập nhật preference của current user.
     */
    @PutMapping("/me")
    public ResponseEntity<UserPreferenceResponse> save(
            @Valid @RequestBody UserPreferenceRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        log.info("PUT /api/preferences/me - userId: {}", userId);

        UserPreferenceResponse response = userPreferenceService.save(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/preferences/me
     * Lấy preference của current user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserPreferenceResponse> get(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/preferences/me - userId: {}", userId);

        UserPreferenceResponse response = userPreferenceService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/preferences/me
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/preferences/me - userId: {}", userId);

        userPreferenceService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
