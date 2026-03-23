package com.example.Dating.controller;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.request.PhotoSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * POST /api/messages/send
     * Body: { "conversationId": "uuid", "senderId": "uuid", "content": "..." }
     * Response 200: MessageResponse
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> send(
            @RequestBody MessageSendRequest request) {
        log.info("POST /api/messages/send - conversationId: {}, senderId: {}",
                request.getConversationId(), request.getSenderId());
        MessageResponse response = messageService.send(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/photo")
    public ResponseEntity<MessageResponse> sendPhoto(
            @ModelAttribute PhotoSendRequest request
            ){
        log.info("POST /api/messages/photo - conversationId: {}, senderId: {}",
                request.getConversationId(), request.getSenderId());
        MessageResponse response = messageService.sendPhoto(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/messages/{conversationId}?viewerId={userId}
     * Lấy tin nhắn theo góc nhìn của viewerId:
     *  - Bỏ qua tin nhắn viewerId đã "Delete for me"
     *  - Tin nhắn đã unsent: content = null, unsent = true
     * Response 200: List<MessageResponse>
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID conversationId,
            @RequestParam UUID viewerId) {
        log.info("GET /api/messages/{} - viewerId: {}", conversationId, viewerId);
        List<MessageResponse> responses = messageService.getMessages(conversationId, viewerId);
        return ResponseEntity.ok(responses);
    }

    /**
     * DELETE /api/messages/{messageId}/delete-for-me?requesterId={userId}
     * Xóa tin nhắn khỏi view của requesterId.
     * Người còn lại không bị ảnh hưởng. Không broadcast WS.
     * Chỉ sender được phép.
     * Response 204: No Content
     */
    @DeleteMapping("/{messageId}/delete-for-me")
    public ResponseEntity<Void> deleteForMe(
            @PathVariable UUID messageId,
            @RequestParam UUID requesterId) {
        log.info("DELETE /api/messages/{}/delete-for-me - requesterId: {}", messageId, requesterId);
        messageService.deleteForMe(messageId, requesterId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/messages/{messageId}/unsend?requesterId={userId}
     * Thu hồi tin nhắn với tất cả. content = null, unsent = true.
     * Service publish event → MessageEventListener broadcast WS.
     * Chỉ sender được phép.
     * Response 204: No Content
     */
    @DeleteMapping("/{messageId}/unsend")
    public ResponseEntity<Void> unsendForEveryone(
            @PathVariable UUID messageId,
            @RequestParam UUID requesterId) {
        log.info("DELETE /api/messages/{}/unsend - requesterId: {}", messageId, requesterId);
        messageService.unsendForEveryone(messageId, requesterId);
        return ResponseEntity.noContent().build();
    }
}