package com.example.Dating.service;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.*;
import com.example.Dating.events.MessageUnsendEvent;
import com.example.Dating.exception.DuplicateResourceException;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.mapper.MessageMapper;
import com.example.Dating.policy.UnsendPolicy;
import com.example.Dating.repository.ConversationRepository;
import com.example.Dating.repository.MessageDeletionRepository;
import com.example.Dating.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository         messageRepository;
    private final MessageDeletionRepository deletionRepository;
    private final ConversationRepository    conversationRepository;
    private final UserMatchService          userMatchService;
    private final AuthService        userService;
    private final ApplicationEventPublisher eventPublisher;
    private final UnsendPolicy              unsendPolicy;

    @Override
    @Transactional
    public MessageResponse send(MessageSendRequest request) {
        Conversation conversation = findConversationOrThrow(request.getConversationId());
        User sender = userService.findById(request.getSenderId());

        validateMembership(conversation, sender.getUserId());
        validateActiveMatch(conversation, sender.getUserId());

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .build();

        messageRepository.save(message);
        log.info("Message saved - id: {}, conversationId: {}", message.getId(), conversation.getId());

        return MessageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> getMessages(UUID conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(MessageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID conversationId, UUID viewerId) {
        List<Message> messages = messageRepository
                .findByConversationIdOrderBySentAtAsc(conversationId);

        // query lấy toàn bộ messageId mà viewerId đã "delete for me"
        Set<UUID> deletedIds = deletionRepository
                .findDeletedMessageIdsByUserInConversation(viewerId, conversationId);

        return messages.stream()
                .filter(msg -> !deletedIds.contains(msg.getId()))
                .map(MessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteForMe(UUID messageId, UUID requesterId) {
        log.info("deleteForMe - messageId: {}, requesterId: {}", messageId, requesterId);

        Message message = findMessageOrThrow(messageId);
        validateSender(message, requesterId);

        if (Boolean.TRUE.equals(message.getUnsent())) {
            throw new ValidationException("Cannot delete a message that has already been unsent for everyone");
        }
        if (deletionRepository.existsByMessage_IdAndUserId(messageId, requesterId)) {
            throw new DuplicateResourceException("Message is already deleted for you");
        }

        deletionRepository.save(MessageDeletion.builder()
                .message(message)
                .userId(requesterId)
                .build());

        log.info("Message {} deleted for user {}", messageId, requesterId);
    }

    @Override
    @Transactional
    public void unsendForEveryone(UUID messageId, UUID requesterId) {
        log.info("unsendForEveryone - messageId: {}, requesterId: {}", messageId, requesterId);

        Message message = findMessageOrThrow(messageId);
        validateSender(message, requesterId);

        // Tất cả điều kiện nghiệp vụ nằm trong policy
        // Service không biết chi tiết — chỉ gọi validate()
        unsendPolicy.validate(message);

        message.setUnsent(true);
        messageRepository.save(message);
        log.info("Message {} unsent by {}", messageId, requesterId);

        eventPublisher.publishEvent(new MessageUnsendEvent(
                this, messageId, message.getConversation().getId(), requesterId));
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Conversation findConversationOrThrow(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + id));
    }

    private Message findMessageOrThrow(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + id));
    }

    private void validateMembership(Conversation conv, UUID userId) {
        boolean isMember = conv.getUserA().getUserId().equals(userId)
                || conv.getUserB().getUserId().equals(userId);
        if (!isMember) {
            throw new ValidationException("User is not a member of this conversation");
        }
    }

    private void validateActiveMatch(Conversation conv, UUID senderId) {
        UUID otherId = conv.getUserA().getUserId().equals(senderId)
                ? conv.getUserB().getUserId()
                : conv.getUserA().getUserId();
        if (!userMatchService.hasActiveMatch(senderId, otherId)) {
            throw new ValidationException("You can only message active matches");
        }
    }

    private void validateSender(Message message, UUID requesterId) {
        if (!message.getSender().getUserId().equals(requesterId)) {
            throw new ValidationException("Only the original sender can perform this action");
        }
    }
}