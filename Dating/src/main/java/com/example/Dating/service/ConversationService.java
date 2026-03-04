package com.example.Dating.service;

import com.example.Dating.dtos.request.ConversationCreateRequest;
import com.example.Dating.dtos.response.ConversationResponse;

import java.util.List;
import java.util.UUID;

public interface ConversationService {

    ConversationResponse create(ConversationCreateRequest request);

    List<ConversationResponse> getUserConversations(UUID userId);
}