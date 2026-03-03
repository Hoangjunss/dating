package com.example.Dating.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserPresenceResponse {

    private UUID userId;
    private boolean online;
    private Instant lastActiveAt;
}