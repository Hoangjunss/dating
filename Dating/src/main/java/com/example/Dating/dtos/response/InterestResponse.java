package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InterestResponse {
    private UUID id;
    private String name;
}