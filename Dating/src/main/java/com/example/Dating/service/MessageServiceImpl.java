package com.example.Dating.service;

import com.example.Dating.dtos.request.MessageSendRequest;
import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.Message;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.MessageMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.ConversationRepository;
import com.example.Dating.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final ConversationRepository conversationRepository;

    private final UserProfileService  userProfileService;

    @Override
    public MessageResponse send(MessageSendRequest request) {

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found for id : " + request.getConversationId()));

        UserProfile userSender = UserProfileMapper.toEntity(userProfileService.get(request.getSenderId()));

        boolean isMember = conversation.getUserA().getUserId().equals(userSender.getUserId())
                || conversation.getUserB().getUserId().equals(userSender.getUserId());

        if (!isMember) {
            throw new RuntimeException("User is not part of this conversation");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(userSender)
                .content(request.getContent())
                .build();

        repository.save(message);

        return MessageMapper.toResponse(message);
    }

    @Override
    public List<MessageResponse> getMessages(UUID conversationId) {
        return repository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(MessageMapper::toResponse)
                .toList();
    }
}