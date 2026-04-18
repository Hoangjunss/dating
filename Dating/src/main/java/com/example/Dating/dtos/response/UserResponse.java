package com.example.Dating.dtos.response;

import com.example.Dating.entities.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private UUID userId;
    private String username;
    private String email;
    private Instant createdAt;

}
