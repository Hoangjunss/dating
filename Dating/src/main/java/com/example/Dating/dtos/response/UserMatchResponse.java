package com.example.Dating.dtos.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatchResponse {

    private UUID id;
    private UUID userAId;
    private UUID userBId;
    private Boolean active;
    private Instant matchedAt;
}