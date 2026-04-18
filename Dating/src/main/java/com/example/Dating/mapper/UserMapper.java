package com.example.Dating.mapper;


import com.example.Dating.dtos.response.UserResponse;
import com.example.Dating.entities.User;

public final class UserMapper {
    public UserMapper() {}

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
