package com.example.Dating.mapper;

import com.example.Dating.dtos.request.UserInterestRequest;
import com.example.Dating.dtos.response.UserInterestResponse;
import com.example.Dating.entities.UserInterest;

public final class UserInterestMapper {

    private UserInterestMapper() {}

    public static UserInterest toEntity(UserInterestRequest r) {
        return UserInterest.builder()
                .build();
    }

    public static UserInterestResponse toResponse(UserInterest e) {
        return UserInterestResponse.builder()
                .id(e.getId())
                .build();
    }
}