package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.ConversationMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository repository;

    private final UserProfileService userProfileService;

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

        UserProfile userA = UserProfileMapper.toEntity(userProfileService.get(first));
        UserProfile userB = UserProfileMapper.toEntity(userProfileService.get(b));

        Conversation conversation = Conversation.builder()
                .userA(userA)
                .userB(userB)
                .build();

        repository.save(conversation);

        return ConversationMapper.toResponse(conversation);
    }

    @Override
    public ConversationResponse findById(UUID id) {
        return repository.findById(id)
                .map(ConversationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No conversation found with id: " + id));
    }

    @Override
    public List<ConversationResponse> getUserConversations(UUID userId) {
        return repository.findByUserAIdOrUserBId(userId, userId)
                .stream()
                .map(ConversationMapper::toResponse)
                .toList();
    }
}