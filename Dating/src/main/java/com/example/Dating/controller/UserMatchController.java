package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.service.UserMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST APIs for match management.
 */
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class UserMatchController {

    private final UserMatchService service;

    // create match manually (normally called internally by swipe logic)
    @PostMapping
    public UserMatchResponse create(
            @RequestParam UUID userA,
            @RequestParam UUID userB
    ) {
        return service.create(userA, userB);
    }

    // get all matches of a user
    @GetMapping("/{userId}")
    public List<UserMatchResponse> getMatches(@PathVariable UUID userId) {
        return service.getMatches(userId);
    }

    // unmatch
    @DeleteMapping("/{matchId}")
    public void unmatch(@PathVariable UUID matchId) {
        service.unmatch(matchId);
    }
}