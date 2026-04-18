package com.example.Dating.mapper;

import com.example.Dating.dtos.response.MessageResponse;
import com.example.Dating.entities.Message;

/**
 * Mapper thuần — chỉ chuyển entity → DTO.
 *
 * Không filter, không biết viewerId, không chứa business logic.
 * Việc lọc "Delete for me" và ẩn content khi unsent
 * là trách nhiệm của MessageServiceImpl.
 */
public final class MessageMapper {

    private MessageMapper() {}

    public static MessageResponse toResponse(Message message) {

        if(message == null) return null;

        boolean isUnsent = Boolean.TRUE.equals(message.getUnsent());

        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getUserId())
                // Content ẩn khi unsent — client dùng field unsent để hiện placeholder
                .content(isUnsent && message.getContent()!=null ? null : message.getContent())
                .type(message.getType())
                .photo(message.getPhoto() != null ? message.getPhoto().getImageUrl() :  null)
                .seen(message.getSeen())
                .sentAt(message.getSentAt())
                .unsent(isUnsent)
                .build();
    }
}