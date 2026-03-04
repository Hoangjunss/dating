package com.example.Dating.mapper;

import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.Message;

public final class MessageMapper {

    private MessageMapper() {}

    public static MessageResponse toResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .content(m.getContent())
                .seen(m.getSeen())
                .sentAt(m.getSentAt())
                .build();
    }
}