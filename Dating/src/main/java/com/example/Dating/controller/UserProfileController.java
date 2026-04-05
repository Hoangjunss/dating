package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * POST /api/profiles
     * Tạo profile sau khi register (bước 2).
     */
    @PostMapping
    public ResponseEntity<UserProfileResponse> create(
            @Valid @RequestBody UserProfileCreateRequest request,
            Authentication auth) {

        UUID jwtUserId = (UUID) auth.getPrincipal();

        // Đảm bảo user chỉ tạo profile cho chính mình
        if (!jwtUserId.equals(request.getUserId())) {
            throw new ValidationException("You can only create a profile for your own account");
        }

        log.info("POST /api/profiles - userId: {}", jwtUserId);
        UserProfileResponse response = userProfileService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/profiles/{userId}
     * Lấy profile công khai của bất kỳ user nào (đã authenticated).
     * Thông tin nhạy cảm (lat/lon chính xác) nên được ẩn trong response.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> get(@PathVariable UUID userId) {
        log.info("GET /api/profiles/{} - Fetching profile", userId);
        UserProfileResponse response = userProfileService.get(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/profiles/me/paginated?page=0&size=10
     * Lấy danh sách profiles khả năng match, lọc theo preference của current user.
     */
    @GetMapping("/me/paginated")
    public ResponseEntity<Page<UserProfileResponse>> getAllPaginated(
            Pageable pageable,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/profiles/me/paginated - userId: {}, page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<UserProfileResponse> responses = userProfileService.getAllPaginated(userId, pageable);
        return ResponseEntity.ok(responses);
    }

    /**
     * PUT /api/profiles/me
     * Cập nhật profile của current user.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> update(
            @Valid @RequestBody UserProfileUpdateRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        log.info("PUT /api/profiles/me - userId: {}", userId);

        UserProfileResponse response = userProfileService.update(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/profiles/me
     * Xóa profile của current user.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/profiles/me - userId: {}", userId);

        userProfileService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
