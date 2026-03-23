package com.example.Dating.controller;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * GET /api/v1/conversations/user/{userId}
     * Lấy danh sách hội thoại của User, phân trang và sắp xếp theo lastActivityAt
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ConversationResponse>> getUserConversations(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("GET /api/v1/conversations/user/{} - page: {}, size: {}", userId, page, size);

        Page<ConversationResponse> response = conversationService.getUserConversations(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/conversations
     * Body: { "userAId": "uuid", "userBId": "uuid" }
     * Response 201: ConversationResponse
     */
    @PostMapping
    public ResponseEntity<ConversationResponse> create(
            @RequestBody ConversationCreateRequest request) {
        log.info("POST /api/conversations - userAId: {}, userBId: {}",
                request.getUserAId(), request.getUserBId());
        ConversationResponse response = conversationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/conversations/{userId}
     * Response 200: List<ConversationResponse>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @PathVariable UUID userId) {
        log.info("GET /api/conversations/{} - Fetching conversations", userId);
        List<ConversationResponse> responses = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/conversations/detail/{conversationId}
     * Response 200: ConversationResponse
     */
    @GetMapping("/detail/{conversationId}")
    public ResponseEntity<ConversationResponse> getById(
            @PathVariable UUID conversationId) {
        log.info("GET /api/conversations/detail/{} - Fetching conversation", conversationId);
        ConversationResponse response = conversationService.findById(conversationId);
        return ResponseEntity.ok(response);
    }
}