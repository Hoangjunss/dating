package com.example.Dating.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InterestCreateRequest {
    
    @NotBlank(message = "Interest name cannot be blank")
    @Size(min = 2, max = 100, message = "Interest name must be between 2 and 100 characters")
    private String name;
}