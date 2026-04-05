package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Request body để lấy access token mới bằng refresh token.
 * POST /api/auth/refresh
 */
@Data
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}
