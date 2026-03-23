package com.example.Dating.mapper;

import com.example.Dating.dtos.response.ConversationResponse;
import com.example.Dating.entities.Conversation;

public final class ConversationMapper {

    private ConversationMapper() {}

    public static ConversationResponse toResponse(Conversation e) {
        return ConversationResponse.builder()
                .id(e.getId())
                .userAId(e.getUserA().getUserId())
                .userBId(e.getUserB().getUserId())
                .createdAt(e.getCreatedAt())
                .build();
    }
}