package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SwipeResponse {
    private UUID id;
    private boolean liked;
}