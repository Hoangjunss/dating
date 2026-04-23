package com.example.Dating.controller;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.service.UserSwipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class UserSwipeController {

    private final UserSwipeService userSwipeService;

    /**
     * POST /api/swipes
     * Body: { "toUserId": "uuid", "isLiked": true|false }
     */
    @PostMapping
    public ResponseEntity<SwipeResultResponse> swipe(
            @RequestBody SwipeRequest request,
            Authentication auth) {

        UUID fromUserId = (UUID) auth.getPrincipal();

        log.info("POST /api/swipes - fromUserId: {}, toUserId: {}, liked: {}",
                fromUserId, request.getToUserId(), request.isLiked());

        SwipeResultResponse response = userSwipeService.swipe(request, fromUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/swipes/match?userB={uuid}
     * Kiểm tra current user có match với userB không.
     */
    @GetMapping("/match")
    public ResponseEntity<Boolean> isMatch(
            @RequestParam UUID userB,
            Authentication auth) {

        UUID userA = (UUID) auth.getPrincipal();
        log.info("GET /api/swipes/match - userA: {}, userB: {}", userA, userB);

        boolean result = userSwipeService.isMatch(userA, userB);
        return ResponseEntity.ok(result);
    }
}
