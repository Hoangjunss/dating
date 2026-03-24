package com.example.Dating.controller;

import com.example.Dating.dtos.response.CandidateResponse;
import com.example.Dating.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Recommendation endpoint — returns ranked candidate profiles to swipe.
 *
 * GET /api/recommendations?userId={uuid}&page=0&size=10
 *
 * Default sort is by composite recommendation score (handled inside the service).
 * Clients should always consume page 0 first; as user swipes, old candidates fall
 * off and new ones appear on subsequent pages.
 */
@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Returns a page of recommended user profiles.
     *
     * Query params:
     *  userId  — required, UUID of the requesting user
     *  page    — 0-based page index (default 0)
     *  size    — page size (default 10, max 50)
     *
     * Response: Page<CandidateResponse>
     */
    @GetMapping
    public ResponseEntity<Page<CandidateResponse>> getRecommendations(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0")  int page,
            // Giữ nguyên = 0, vì nếu tăng số thì sẽ hiểu là bỏ qua 10 đối tượng tốt nhất lần gọi API đó.
            // Vì hiện tại mỗi lần kêu API là mỗi lần chạy lại service recommendation, thì sẽ điểm lại
            @RequestParam(defaultValue = "10") int size
    ) {
        // Cap page size to prevent abuse
        int cappedSize = Math.min(size, 50);

        log.info("GET /api/recommendations - userId={}, page={}, size={}", userId, page, cappedSize);

        Pageable pageable = PageRequest.of(page, cappedSize);
        Page<CandidateResponse> result = recommendationService.getCandidates(userId, pageable);

        return ResponseEntity.ok(result);
    }
}