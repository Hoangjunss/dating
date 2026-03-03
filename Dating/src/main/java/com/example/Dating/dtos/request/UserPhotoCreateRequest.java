package com.example.Dating.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserPhotoCreateRequest {
    private UUID userId;
    private String url;
    private Integer sortOrder;
    private Boolean isPrimary;
}