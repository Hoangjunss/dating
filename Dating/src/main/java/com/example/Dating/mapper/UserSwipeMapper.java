package com.example.Dating.mapper;

import com.example.Dating.dtos.request.SwipeRequest;
import com.example.Dating.dtos.response.SwipeResponse;
import com.example.Dating.entities.UserSwipe;

import java.time.Instant;

public final class UserSwipeMapper {

    private UserSwipeMapper() {}

    public static UserSwipe toEntity(SwipeRequest r) {
        return UserSwipe.builder()
                .fromUserId(r.getFromUserId())
                .toUserId(r.getToUserId())
                .liked(r.isLiked())
                .createdAt(Instant.now())
                .build();
    }

    public static SwipeResponse toResponse(UserSwipe e) {
        return SwipeResponse.builder()
                .id(e.getId())
                .liked(e.isLiked())
                .build();
    }
}