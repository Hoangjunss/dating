package com.example.Dating.controller;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
     * GET /api/conversations/me/paginated?page=0&size=10
     **Lấy danh sách hội thoại của User
     * userId lấy từ JWT — client không thể xem conversation của người khác
     */
    @GetMapping("/me/paginated")
    public ResponseEntity<Page<ConversationResponse>> getMyConversationsPaginated(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/conversations/me/paginated - userId: {}, page: {}, size: {}",
                userId, page, size);

        Page<ConversationResponse> response = conversationService.getUserConversations(userId, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/conversations
     * Body: { "userBId": "uuid" }  ← chỉ cần gửi đối phương, userAId = current user
     * Response 201: ConversationResponse
     */
    @PostMapping
    public ResponseEntity<ConversationResponse> create(
            @RequestBody ConversationCreateRequest request,
            Authentication auth) {

        UUID userId = (UUID) auth.getPrincipal();
        request.setUserAId(userId);   // Override — không tin client

        log.info("POST /api/conversations - userAId: {}, userBId: {}",
                userId, request.getUserBId());

        ConversationResponse response = conversationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/conversations/me
     * Response 200: List<ConversationResponse>
     */
    @GetMapping("/me")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        log.info("GET /api/conversations/me - userId: {}", userId);

        List<ConversationResponse> responses = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(responses);
    }


    /**
     * GET /api/conversations/detail/{conversationId}
     * chỉ member của conversation mới có quyền xem
     * Validation thực hiện trong service
     * Response 200: ConversationResponse
     */
    @GetMapping("/detail/{conversationId}")
    public ResponseEntity<ConversationResponse> getById(
            @PathVariable UUID conversationId,
            Authentication auth) {

        UUID requesterId = (UUID) auth.getPrincipal();
        log.info("GET /api/conversations/detail/{} - requesterId: {}", conversationId, requesterId);

        // Service sẽ validate membership trước khi trả kết quả
        ConversationResponse response = conversationService.findById(conversationId, requesterId);
        return ResponseEntity.ok(response);
    }
}