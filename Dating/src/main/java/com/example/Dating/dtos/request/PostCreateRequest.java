package com.example.Dating.dtos.request;

import lombok.Data;

import java.util.UUID;

// PostCreateRequest.java
@Data
public class PostCreateRequest {
    private UUID userId;
    private String content;
    private String imageUrl;
}

// PostResponse.java
