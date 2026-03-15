package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UserInterestRequest {
    
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    
    @NotNull(message = "Interest ID cannot be null")
    private UUID interestId;
}