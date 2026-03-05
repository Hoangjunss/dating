package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.service.UserPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User Photo Management.
 * Provides endpoints for managing user photos and gallery.
 */
@Slf4j
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class UserPhotoController {

    private final UserPhotoService userPhotoService;

    /**
     * Uploads a new photo for a user.
     * 
     * @param request Photo upload request
     * @return Created photo response with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<UserPhotoResponse> create(
            @Valid @RequestBody UserPhotoCreateRequest request) {
        log.info("POST /api/photos - Uploading photo for user: {}", request.getUserId());
        
        UserPhotoResponse response = userPhotoService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves a specific photo by ID.
     * 
     * @param id The photo ID (UUID)
     * @return Photo details with 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserPhotoResponse> get(
            @PathVariable UUID id) {
        log.info("GET /api/photos/{} - Fetching photo", id);
        
        UserPhotoResponse response = userPhotoService.get(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all photos for a specific user.
     * 
     * @param userId The user ID (UUID)
     * @return List of user's photos
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPhotoResponse>> getByUser(
            @PathVariable UUID userId) {
        log.info("GET /api/photos/user/{} - Fetching photos for user", userId);
        
        List<UserPhotoResponse> responses = userPhotoService.getByUser(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Deletes a photo by ID.
     * 
     * @param id The photo ID (UUID)
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        log.info("DELETE /api/photos/{} - Deleting photo", id);
        
        userPhotoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}