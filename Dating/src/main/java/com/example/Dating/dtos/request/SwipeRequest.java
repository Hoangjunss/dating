package com.example.Dating.dtos.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SwipeRequest {
    private UUID fromUserId;
    private UUID toUserId;
    private boolean liked;
}