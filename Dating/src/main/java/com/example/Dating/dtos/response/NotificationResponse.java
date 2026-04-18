package com.example.Dating.dtos.response;

import com.example.Dating.entities.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private UUID id;
    private NotificationType type;
    private String title;
    private String body;
    private UUID conversationId;
    private UUID relatedUserId;
    private boolean read;
    private Instant createdAt;
}
