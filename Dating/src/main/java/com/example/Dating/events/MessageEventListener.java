package com.example.Dating.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Lắng nghe các Message-related event và broadcast qua WebSocket.
 *
 * Tách biệt hoàn toàn khỏi Service — Service chỉ publish event,
 * không biết gì về WS hay cách deliver.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Khi có MessageUnsendEvent → broadcast xuống tất cả client trong conversation.
     *
     * Client nhận payload:
     * {
     *   "type": "UNSEND",
     *   "messageId": "uuid",
     *   "conversationId": "uuid"
     * }
     * → tìm message theo id, thay content bằng placeholder "Tin nhắn đã bị thu hồi"
     */
    @Async
    @EventListener
    public void handleUnsendEvent(MessageUnsendEvent event) {
        log.debug("Broadcasting UNSEND event - messageId: {}, conversationId: {}",
                event.getMessageId(), event.getConversationId());

        messagingTemplate.convertAndSend(
                "/topic/conversation." + event.getConversationId(),
                (Object) Map.of(
                        "type",           "UNSEND",
                        "messageId",      event.getMessageId().toString(),
                        "conversationId", event.getConversationId().toString()
                )
        );
    }
}