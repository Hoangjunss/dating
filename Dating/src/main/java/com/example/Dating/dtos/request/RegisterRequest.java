package com.example.Dating.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_.-]+$",
            message = "Username can only contain letters, numbers, underscores, dots, and hyphens"
    )
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    /**
     * Password policy:
     *  - Tối thiểu 8 ký tự
     *  - Phải có ít nhất 1 chữ hoa, 1 chữ thường, 1 chữ số, 1 ký tự đặc biệt
     *
     * Regex giải thích:
     *  (?=.*[a-z])   — ít nhất 1 chữ thường
     *  (?=.*[A-Z])   — ít nhất 1 chữ hoa
     *  (?=.*\d)      — ít nhất 1 chữ số
     *  (?=.*[@$!%*?&]) — ít nhất 1 ký tự đặc biệt
     *  .{8,100}      — độ dài 8–100 ký tự
     */
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
    )
    private String password;
}
