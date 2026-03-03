package com.example.Dating.controller;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.service.UserSwipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class UserSwipeController {

    private final UserSwipeService service;

    @PostMapping
    public SwipeResponse swipe(@RequestBody SwipeRequest r) {
        return service.swipe(r);
    }

    @GetMapping("/match")
    public boolean isMatch(
            @RequestParam UUID userA,
            @RequestParam UUID userB) {
        return service.isMatch(userA, userB);
    }
}