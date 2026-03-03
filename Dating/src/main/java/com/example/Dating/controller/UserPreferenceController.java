package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserPreferenceRequest;
import com.example.Dating.dtos.response.UserPreferenceResponse;
import com.example.Dating.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService service;

    @PutMapping("/{userId}")
    public UserPreferenceResponse save(
            @PathVariable UUID userId,
            @RequestBody UserPreferenceRequest r) {
        return service.save(userId, r);
    }

    @GetMapping("/{userId}")
    public UserPreferenceResponse get(@PathVariable UUID userId) {
        return service.get(userId);
    }
}