package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserInterestResponse {
    private UUID id;
    private UUID interestId;
}