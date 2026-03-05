package com.example.Dating.controller;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.service.InterestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Interest Management.
 * Provides endpoints for managing interests/hobbies (master data).
 */
@Slf4j
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {

    private final InterestService interestService;

    /**
     * Creates a new interest.
     * 
     * @param request Interest creation request
     * @return Created interest response with 201 CREATED status
     */
    @PostMapping
    public ResponseEntity<InterestResponse> create(
            @Valid @RequestBody InterestCreateRequest request) {
        log.info("POST /api/interests - Creating interest: {}", request.getName());
        
        InterestResponse response = interestService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves a specific interest by ID.
     * 
     * @param id The interest ID (UUID)
     * @return Interest details with 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<InterestResponse> get(
            @PathVariable UUID id) {
        log.info("GET /api/interests/{} - Fetching interest", id);
        
        InterestResponse response = interestService.get(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all available interests.
     * 
     * @return List of all interests
     */
    @GetMapping
    public ResponseEntity<List<InterestResponse>> getAll() {
        log.info("GET /api/interests - Fetching all interests");
        
        List<InterestResponse> responses = interestService.getAll();
        return ResponseEntity.ok(responses);
    }

    /**
     * Deletes an interest by ID.
     * 
     * @param id The interest ID (UUID)
     * @return 204 NO_CONTENT status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        log.info("DELETE /api/interests/{} - Deleting interest", id);
        
        interestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}