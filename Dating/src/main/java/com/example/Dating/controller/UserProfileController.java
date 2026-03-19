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

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * POST /api/profiles
     * Tạo profile sau khi đã register (bước 2).
     * Body: { "userId": "uuid", "displayName": "...", "gender": "MALE|FEMALE|OTHER",
     *         "birthday": "yyyy-MM-dd", ... }
     * Response 201: UserProfileResponse
     */
    @PostMapping
    public ResponseEntity<UserProfileResponse> create(
            @Valid @RequestBody UserProfileCreateRequest request) {
        log.info("POST /api/profiles - Creating profile for userId: {}", request.getUserId());
        UserProfileResponse response = userProfileService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/profiles/{userId}
     * Lấy profile theo userId.
     * Response 200: UserProfileResponse
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> get(
            @PathVariable UUID userId) {
        log.info("GET /api/profiles/{} - Fetching profile", userId);
        UserProfileResponse response = userProfileService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/profiles
     * Lấy tất cả profiles (không phân trang).
     * Response 200: List<UserProfileResponse>
     */
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAll() {
        log.info("GET /api/profiles - Fetching all profiles");
        List<UserProfileResponse> responses = userProfileService.getAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/profiles/{userId}/paginated?page=0&size=10
     * Lấy profiles có phân trang, lọc theo preference của userId.
     * Response 200: Page<UserProfileResponse>
     */
    @GetMapping("/{userId}/paginated")
    public ResponseEntity<Page<UserProfileResponse>> getAllPaginated(
            @PathVariable UUID userId,
            Pageable pageable) {
        log.info("GET /api/profiles/{}/paginated - page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<UserProfileResponse> responses = userProfileService.getAllPaginated(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * PUT /api/profiles/{userId}
     * Cập nhật profile (partial update — chỉ field không null mới được ghi).
     * Response 200: UserProfileResponse
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> update(
            @PathVariable UUID userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        log.info("PUT /api/profiles/{} - Updating profile", userId);
        UserProfileResponse response = userProfileService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/profiles/{userId}
     * Xóa profile.
     * Response 204: No Content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID userId) {
        log.info("DELETE /api/profiles/{} - Deleting profile", userId);
        userProfileService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}