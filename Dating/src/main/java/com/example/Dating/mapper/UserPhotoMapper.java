package com.example.Dating.mapper;

import com.example.Dating.dtos.request.UserPhotoCreateRequest;
import com.example.Dating.dtos.response.UserPhotoResponse;
import com.example.Dating.entities.UserPhoto;

public final class UserPhotoMapper {

    private UserPhotoMapper() {}

    public static UserPhoto toEntity(UserPhotoCreateRequest r) {
        return UserPhoto.builder()
                .userId(r.getUserId())
                .url(r.getUrl())
                .sortOrder(r.getSortOrder())
                .isPrimary(Boolean.TRUE.equals(r.getIsPrimary()))
                .build();
    }

    public static UserPhotoResponse toResponse(UserPhoto e) {
        return UserPhotoResponse.builder()
                .id(e.getId())
                .url(e.getUrl())
                .sortOrder(e.getSortOrder())
                .isPrimary(e.isPrimary())
                .build();
    }
}