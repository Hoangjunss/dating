package com.example.Dating.controller;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.service.UserInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService service;

    @PostMapping
    public UserInterestResponse add(@RequestBody UserInterestRequest r) {
        return service.add(r);
    }

    @GetMapping("/{userId}")
    public List<UserInterestResponse> getByUser(@PathVariable UUID userId) {
        return service.getByUser(userId);
    }

    @DeleteMapping
    public void remove(@RequestParam UUID userId,
                       @RequestParam UUID interestId) {
        service.remove(userId, interestId);
    }
}