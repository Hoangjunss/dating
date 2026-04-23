package com.example.Dating.service.impl;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.User;
import com.example.Dating.exception.ResourceNotFoundException;
import com.example.Dating.exception.ValidationException;
import com.example.Dating.mapper.ConversationMapper;
import com.example.Dating.repository.ConversationRepository;
import com.example.Dating.service.AuthService;
import com.example.Dating.service.ConversationService;
import com.example.Dating.service.UserMatchService;
import com.example.Dating.specification.ConversationSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository repository;
    private final UserMatchService userMatchService;
    private final AuthService userService;

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

        User userA = userService.findById(first);
        User userB = userService.findById(second);

        Conversation conversation = Conversation.builder()
                .userA(userA)
                .userB(userB)
                .build();

        repository.save(conversation);
        return ConversationMapper.toResponse(conversation);
    }
    @Override
    public Page<ConversationResponse> getUserConversations(UUID userId, int page, int size) {
        // 1. Tạo Pageable (Phân trang)
        // Lưu ý: Sắp xếp đã được định nghĩa trong Specification hoặc bạn có thể
        // định nghĩa ở đây nếu Specification chỉ làm nhiệm vụ lọc.
        Pageable pageable = PageRequest.of(page, size);

        // 2. Lấy Specification lọc theo User và sắp xếp theo lastActivityAt
        Specification<Conversation> spec = ConversationSpecification.findUserConversations(userId);

        // 3. Thực thi truy vấn
        return repository.findAll(spec, pageable)
                .map(ConversationMapper::toResponse);
    }

    @Override
    @Transactional
    public ConversationResponse createOrGet(UUID user1, UUID user2, boolean skipMatchValidation) {
        UUID first  = user1.compareTo(user2) < 0 ? user1 : user2;
        UUID second = user1.compareTo(user2) < 0 ? user2 : user1;

        Optional<Conversation> existing = repository.findByUserAUserIdAndUserBUserId(first, second);
        if (existing.isPresent()) {
            return ConversationMapper.toResponse(existing.get());
        }
        if (!skipMatchValidation &&
                !userMatchService.hasActiveMatch(user1, user2)) {
            throw new IllegalStateException(
                    "Conversation can only be created between matched users"
            );
        }

        User userA = userService.findById(first);
        User userB = userService.findById(second);

        Conversation conv = Conversation.builder()
                .userA(userA)
                .userB(userB)
                .build();

        conv = repository.save(conv);
        return ConversationMapper.toResponse(conv);
    }

    @Override
    public ConversationResponse findById(UUID id, UUID requesterId) {

        Conversation conv = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No conversation found with id: " + id));

        boolean isMember = conv.getUserA().getUserId().equals(requesterId)
                || conv.getUserB().getUserId().equals(requesterId);
        if (!isMember) {
            throw new ValidationException("You are not a member of this conversation");
        }

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
                .map(conversation -> {
                    ConversationResponse conversationResponse = ConversationMapper.toResponse(conversation);
                    conversationResponse.setNickName(conversation.nickName(userId));
                    return conversationResponse;
                })
                .toList();
    }
}