package com.example.Dating.controller;

import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.service.UserMatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
     * GET /api/matches/me
     * Lấy tất cả active matches của current user.
     */
    @GetMapping("/me")
    public ResponseEntity<List<UserMatchResponse>> getActiveMatches(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/matches/me - userId: {}", userId);
        List<UserMatchResponse> responses = userMatchService.getActiveMatches(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/matches/me/all
     * Lấy tất cả matches (kể cả đã unmatch) của current user.
     *
     */
    @GetMapping("/me/all")
    public ResponseEntity<List<UserMatchResponse>> getAllMatches(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/matches/me/all - userId: {}", userId);
        List<UserMatchResponse> responses = userMatchService.getAllMatches(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * DELETE /api/matches/{matchId}
     * Unmatch — đánh dấu match là inactive.
     */
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> unmatch(
            @PathVariable UUID matchId,
            Authentication auth) {

        UUID requesterId = (UUID) auth.getPrincipal();
        log.info("DELETE /api/matches/{} - requesterId: {}", matchId, requesterId);

        userMatchService.unmatch(matchId, requesterId);  // Truyền requesterId để validate ownership
        return ResponseEntity.noContent().build();
    }
}
