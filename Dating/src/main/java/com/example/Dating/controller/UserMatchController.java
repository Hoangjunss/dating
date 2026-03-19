package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.service.UserMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class UserMatchController {

    private final UserMatchService userMatchService;

    /**
     * POST /api/matches?userA={uuid}&userB={uuid}
     * Tạo match thủ công (thường được gọi nội bộ bởi swipe logic).
     * Response 201: UserMatchResponse
     */
    @PostMapping
    public ResponseEntity<UserMatchResponse> create(
            @RequestParam UUID userA,
            @RequestParam UUID userB) {
        log.info("POST /api/matches - userA: {}, userB: {}", userA, userB);
        UserMatchResponse response = userMatchService.create(userA, userB);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/matches/{userId}
     * Lấy tất cả active matches của một user.
     * Response 200: List<UserMatchResponse>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserMatchResponse>> getActiveMatches(
            @PathVariable UUID userId) {
        log.info("GET /api/matches/{} - Fetching active matches", userId);
        List<UserMatchResponse> responses = userMatchService.getActiveMatches(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/matches/{userId}/all
     * Lấy tất cả matches (kể cả đã unmatch) của một user.
     * Response 200: List<UserMatchResponse>
     */
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<UserMatchResponse>> getAllMatches(
            @PathVariable UUID userId) {
        log.info("GET /api/matches/{}/all - Fetching all matches", userId);
        List<UserMatchResponse> responses = userMatchService.getAllMatches(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * DELETE /api/matches/{matchId}
     * Unmatch — đánh dấu match là inactive.
     * Response 204: No Content
     */
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> unmatch(
            @PathVariable UUID matchId) {
        log.info("DELETE /api/matches/{} - Unmatching", matchId);
        userMatchService.unmatch(matchId);
        return ResponseEntity.noContent().build();
    }
}