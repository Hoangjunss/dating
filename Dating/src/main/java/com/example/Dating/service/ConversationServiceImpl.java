package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.mapper.ConversationMapper;
import com.example.Dating.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository repository;

    @Override
    public ConversationResponse create(ConversationCreateRequest request) {

        UUID a = request.getUserAId();
        UUID b = request.getUserBId();

        UUID first = a.compareTo(b) < 0 ? a : b;
        UUID second = a.compareTo(b) < 0 ? b : a;

        repository.findByUserAIdAndUserBId(first, second)
                .ifPresent(c -> {
                    throw new RuntimeException("Conversation already exists");
                });

        Conversation conversation = Conversation.builder()
                .userAId(first)
                .userBId(second)
                .build();

        repository.save(conversation);

        return ConversationMapper.toResponse(conversation);
    }

    @Override
    public List<ConversationResponse> getUserConversations(UUID userId) {
        return repository.findByUserAIdOrUserBId(userId, userId)
                .stream()
                .map(ConversationMapper::toResponse)
                .toList();
    }
}