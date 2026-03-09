package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class UserPhotoCreateRequest {
    
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    
    private MultipartFile image;
    
    @Min(value = 1, message = "Sort order must be at least 1")
    private Integer sortOrder;
    
    private Boolean isPrimary;
}