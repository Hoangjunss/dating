package com.example.Dating.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event được publish sau khi một tin nhắn bị unsend for everyone.
 *
 * Service publish event này — không cần biết gì về WebSocket hay Controller.
 * MessageEventListener lắng nghe và thực hiện broadcast WS.
 *
 * Lợi ích:
 *  - Service hoàn toàn độc lập với tầng transport (WS/REST)
 *  - Dễ thêm side-effect sau này (push notification, audit log...)
 *    mà không sửa Service
 */
@Getter
public class MessageUnsendEvent extends ApplicationEvent {

    private final UUID messageId;
    private final UUID conversationId;
    private final UUID requesterId;

    public MessageUnsendEvent(Object source, UUID messageId, UUID conversationId, UUID requesterId) {
        super(source);
        this.messageId      = messageId;
        this.conversationId = conversationId;
        this.requesterId    = requesterId;
    }
}