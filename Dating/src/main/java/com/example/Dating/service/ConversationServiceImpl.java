package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.UserProfile;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.mapper.ConversationMapper;
import com.example.Dating.mapper.UserProfileMapper;
import com.example.Dating.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository repository;
    private final UserMatchService userMatchService;
    private final UserProfileService userProfileService;

    @Override
    @Transactional
    public ConversationResponse create(ConversationCreateRequest request) {
        UUID a = request.getUserAId();
        UUID b = request.getUserBId();

        UUID first = a.compareTo(b) < 0 ? a : b;
        UUID second = a.compareTo(b) < 0 ? b : a;

        if (repository.findByUserA_UserIdAndUserB_UserId(first, second).isPresent()) {
            throw new IllegalStateException("Conversation already exists");
        }

        if (!userMatchService.hasActiveMatch(a, b)) {
            throw new IllegalStateException("Conversation can only be created between matched users");
        }

        UserProfile userA = userProfileService.findEntityById(first);
        UserProfile userB = userProfileService.findEntityById(second);

        Conversation conversation = Conversation.builder()
                .userA(userA)
                .userB(userB)
                .build();

        repository.save(conversation);
        return ConversationMapper.toResponse(conversation);
    }

    @Override
    @Transactional
    public ConversationResponse createOrGet(UUID user1, UUID user2) {
        UUID first  = user1.compareTo(user2) < 0 ? user1 : user2;
        UUID second = user1.compareTo(user2) < 0 ? user2 : user1;

        Optional<Conversation> existing = repository.findByUserAUserIdAndUserBUserId(first, second);
        if (existing.isPresent()) {
            return ConversationMapper.toResponse(existing.get());
        }

        // Check match tồn tại trước khi tạo conversation
        if (!userMatchService.hasActiveMatch(user1, user2)) {
            throw new IllegalStateException("Conversation can only be created between matched users");
        }

        UserProfile u1 = userProfileService.findEntityById(first);
        UserProfile u2 = userProfileService.findEntityById(second);

        Conversation conv = Conversation.builder()
                .userA(u1)
                .userB(u2)
                .build();

        conv = repository.save(conv);
        return ConversationMapper.toResponse(conv);
    }

    @Override
    public ConversationResponse findById(UUID id) {
        return repository.findById(id)
                .map(ConversationMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No conversation found with id: " + id));
    }

    @Override
    public List<ConversationResponse> getUserConversations(UUID userId) {
        return repository.findByUserAUserIdOrUserBUserId(userId, userId)
                .stream()
                .map(ConversationMapper::toResponse)
                .toList();
    }
}