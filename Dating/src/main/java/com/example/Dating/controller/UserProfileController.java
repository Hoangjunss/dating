package com.example.Dating.controller;



import com.example.Dating.dtos.request.UserProfileCreateRequest;
import com.example.Dating.dtos.request.UserProfileUpdateRequest;
import com.example.Dating.dtos.response.UserProfileResponse;
import com.example.Dating.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    // CREATE
    @PostMapping
    public UserProfileResponse create(@RequestBody UserProfileCreateRequest request) {
        return service.create(request);
    }

    // GET ONE
    @GetMapping("/{userId}")
    public UserProfileResponse get(@PathVariable UUID userId) {
        return service.get(userId);
    }

    // GET ALL
    @GetMapping
    public List<UserProfileResponse> getAll() {
        return service.getAll();
    }

    // UPDATE
    @PutMapping("/{userId}")
    public UserProfileResponse update(
            @PathVariable UUID userId,
            @RequestBody UserProfileUpdateRequest request
    ) {
        return service.update(userId, request);
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable UUID userId) {
        service.delete(userId);
    }
}