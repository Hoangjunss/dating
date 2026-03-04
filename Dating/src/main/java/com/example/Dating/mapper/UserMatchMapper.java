package com.example.Dating.mapper;



import com.example.Dating.dtos.response.UserMatchResponse;
import com.example.Dating.entities.UserMatch;

public final class UserMatchMapper {

    private UserMatchMapper() {}

    public static UserMatchResponse toResponse(UserMatch e) {
        return UserMatchResponse.builder()
                .id(e.getId())
                .userAId(e.getUserAId())
                .userBId(e.getUserBId())
                .active(e.getActive())
                .matchedAt(e.getMatchedAt())
                .build();
    }
}