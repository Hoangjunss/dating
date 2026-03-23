package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PostResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String userProfilePic;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;
}