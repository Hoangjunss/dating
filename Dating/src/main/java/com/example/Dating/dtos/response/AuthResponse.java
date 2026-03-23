package com.example.Dating.dtos.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private UUID userId;
    private String username;
    private String email;

    /**
     * true = UserProfile exists → client goes to the main screen
     * false = UserProfile does not exist → client navigates to the profile creation screen
     */
    private Boolean hasProfile;
}