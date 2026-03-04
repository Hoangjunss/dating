package com.example.Dating.dtos.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSendRequest {

    private UUID conversationId;
    private UUID senderId;
    private String content;
}