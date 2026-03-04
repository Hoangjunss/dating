package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class UserPhotoCreateRequest {
    
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    
    @NotBlank(message = "Photo URL cannot be blank")
    @Pattern(regexp = "^(https?://)?[\\w./]+\\.(jpg|jpeg|png|gif|webp)$", 
             message = "URL must be a valid image URL")
    private String url;
    
    @Min(value = 1, message = "Sort order must be at least 1")
    private Integer sortOrder;
    
    private Boolean isPrimary;
}