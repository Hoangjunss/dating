package com.example.Dating.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserInterestRequest {
    private UUID userId;
    private UUID interestId;
}