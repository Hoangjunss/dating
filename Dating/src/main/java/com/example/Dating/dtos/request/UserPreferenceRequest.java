package com.example.Dating.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserPreferenceRequest {

    @NotBlank(message = "Gender preference cannot be blank")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender preference must be MALE, FEMALE, or OTHER")
    private String genderPreference;
    
    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 150, message = "Minimum age cannot exceed 150")
    private Integer minAge;
    
    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 150, message = "Maximum age cannot exceed 150")
    private Integer maxAge;
    
    @Min(value = 1, message = "Maximum distance must be at least 1 km")
    @Max(value = 10000, message = "Maximum distance cannot exceed 10000 km")
    private Integer maxDistanceKm;
}