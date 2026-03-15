package com.example.Dating.controller;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResultResponse;
import com.example.Dating.service.UserSwipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
     * Thực hiện swipe (like hoặc pass).
     * Nếu cả 2 user đều like nhau → tạo match + conversation tự động.
     * Body: { "fromUserId": "uuid", "toUserId": "uuid", "isLiked": true|false }
     * Response 200: SwipeResultResponse
     *   { "id": "uuid", "isLiked": true, "isMutualLike": true,
     *     "matchId": "uuid|null", "conversationId": "uuid|null" }
     */
    @PostMapping
    public ResponseEntity<SwipeResultResponse> swipe(
            @RequestBody SwipeRequest request) {
        log.info("POST /api/swipes - from: {}, to: {}, liked: {}",
                request.getFromUserId(), request.getToUserId(), request.isLiked());
        SwipeResultResponse response = userSwipeService.swipe(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/swipes/match?userA={uuid}&userB={uuid}
     * Kiểm tra 2 user có match với nhau không.
     * Response 200: true | false
     */
    @GetMapping("/match")
    public ResponseEntity<Boolean> isMatch(
            @RequestParam UUID userA,
            @RequestParam UUID userB) {
        log.info("GET /api/swipes/match - userA: {}, userB: {}", userA, userB);
        boolean result = userSwipeService.isMatch(userA, userB);
        return ResponseEntity.ok(result);
    }
}