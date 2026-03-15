package com.example.Dating.controller;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.service.MessageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWsController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Client gửi tin nhắn qua WebSocket.
     * Destination : /app/chat.send
     * Broadcast   : /topic/conversation.{conversationId}
     * Payload: { "conversationId": "uuid", "senderId": "uuid", "content": "..." }
     */
    @MessageMapping("/chat.send")
    public void send(@Payload MessageSendRequest request) {
        log.debug("WS /chat.send - conversationId: {}", request.getConversationId());
        MessageResponse saved = messageService.send(request);
        messagingTemplate.convertAndSend(
                "/topic/conversation." + request.getConversationId(), saved);
    }

    /**
     * Client yêu cầu unsend qua WebSocket.
     * Destination : /app/chat.unsend
     * Payload: { "messageId": "uuid", "conversationId": "uuid", "requesterId": "uuid" }
     * Broadcast được xử lý bởi MessageEventListener sau khi service publish event.
     */
    @MessageMapping("/chat.unsend")
    public void unsend(@Payload UnsendRequest request) {
        log.debug("WS /chat.unsend - messageId: {}, requesterId: {}",
                request.getMessageId(), request.getRequesterId());
        messageService.unsendForEveryone(request.getMessageId(), request.getRequesterId());
    }

    @Data
    public static class UnsendRequest {
        private UUID messageId;
        private UUID conversationId;
        private UUID requesterId;
    }
}