package com.example.Dating.dtos.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationCreateRequest {

    private UUID userAId;
    private UUID userBId;
}