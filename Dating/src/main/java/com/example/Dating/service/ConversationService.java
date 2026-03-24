package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationResponse create(ConversationCreateRequest request);

    ConversationResponse createOrGet(UUID user1, UUID user2, boolean skipMatchValidation);

    ConversationResponse findById(UUID id);

    List<ConversationResponse> getUserConversations(UUID userId);

    Page<ConversationResponse> getUserConversations(UUID userId, int page, int size);
}