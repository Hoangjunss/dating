package com.example.Dating.mapper;

import com.example.Dating.dtos.request.InterestCreateRequest;
import com.example.Dating.dtos.response.InterestResponse;
import com.example.Dating.entities.Interest;

public final class InterestMapper {

    private InterestMapper() {}

    public static Interest toEntity(InterestCreateRequest r) {
        return Interest.builder()
                .name(r.getName())
                .build();
    }

    public static InterestResponse toResponse(Interest e) {
        return InterestResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .build();
    }
}