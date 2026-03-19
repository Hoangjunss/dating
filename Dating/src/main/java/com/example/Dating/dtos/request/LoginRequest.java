package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    /**
     * Users can enter a username OR email here.
     * AuthServiceImpl automatically detects and queries appropriately.
     */
    @NotBlank(message = "Username or email cannot be blank")
    private String identifier;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}