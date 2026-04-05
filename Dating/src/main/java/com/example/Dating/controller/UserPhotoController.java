package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.service.UserPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class UserPhotoController {

    private final UserPhotoService userPhotoService;

    /**
     * POST /api/photos
     * Upload ảnh cho current user.
     */
    @PostMapping
    public ResponseEntity<UserPhotoResponse> create(
            @Valid @RequestBody UserPhotoCreateRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        request.setUserId(userId);   // Override từ JWT

        log.info("POST /api/photos - userId: {}", userId);

        UserPhotoResponse response = userPhotoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/photos/{id}
     * Lấy thông tin ảnh theo photoId (public info).
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserPhotoResponse> get(@PathVariable UUID id) {
        log.info("GET /api/photos/{} - Fetching photo", id);
        UserPhotoResponse response = userPhotoService.get(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/photos/user/{userId}
     * Lấy tất cả ảnh của user (public — để hiển thị profile).
     */

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPhotoResponse>> getByUser(@PathVariable UUID userId) {
        log.info("GET /api/photos/user/{} - Fetching photos", userId);
        List<UserPhotoResponse> responses = userPhotoService.getByUser(userId);
        return ResponseEntity.ok(responses);
    }


    /**
     * DELETE /api/photos/{id}
     * Xóa ảnh.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            Authentication auth) {

        UUID requesterId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/photos/{} - requesterId: {}", id, requesterId);

        userPhotoService.delete(id, requesterId);   // Truyền requesterId để validate ownership
        return ResponseEntity.noContent().build();
    }
}
