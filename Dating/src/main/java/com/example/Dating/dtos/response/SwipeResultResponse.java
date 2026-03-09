package com.example.Dating.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SwipeResultResponse {
    private UUID id;
    private boolean isLiked;
    private boolean isMutualLike;
    private UUID matchId;
    private UUID conversationId;
}
