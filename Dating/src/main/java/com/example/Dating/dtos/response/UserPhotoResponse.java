package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserPhotoResponse {
    private UUID id;
    private String url;
    private Integer sortOrder;
    private boolean isPrimary;
}