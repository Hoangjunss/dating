package com.example.Dating.mapper;

import com.example.Dating.dtos.response.UserPresenceResponse;
import com.example.Dating.entities.UserPresence;

public final class UserPresenceMapper {

    private UserPresenceMapper() {}

    public static UserPresenceResponse toResponse(UserPresence e) {
        return UserPresenceResponse.builder()
                .userId(e.getUserId())
                .online(e.isOnline())
                .lastActiveAt(e.getLastActiveAt())
                .build();
    }
}