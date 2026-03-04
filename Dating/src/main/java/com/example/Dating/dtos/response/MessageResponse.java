package com.example.Dating.dtos.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private UUID id;
    private UUID senderId;
    private String content;
    private Boolean seen;
    private Instant sentAt;
}