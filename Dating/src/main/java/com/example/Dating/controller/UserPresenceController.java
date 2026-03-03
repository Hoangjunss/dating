package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.service.UserPresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class UserPresenceController {

    private final UserPresenceService service;

    @PostMapping("/{userId}/online")
    public void online(@PathVariable UUID userId) {
        service.setOnline(userId);
    }

    @PostMapping("/{userId}/offline")
    public void offline(@PathVariable UUID userId) {
        service.setOffline(userId);
    }

    @GetMapping("/{userId}")
    public UserPresenceResponse get(@PathVariable UUID userId) {
        return service.get(userId);
    }
}