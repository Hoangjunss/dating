package com.example.Dating.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class SwipeRequest {
    private UUID toUserId;

    @JsonProperty("isLiked")
    private boolean isLiked;
}