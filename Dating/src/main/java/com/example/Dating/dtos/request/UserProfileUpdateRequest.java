package com.example.Dating.dtos.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @Size(min = 2, max = 100, message = "Display name must be between 2 and 100 characters")
    private String displayName;
    
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;
    
    @Size(max = 100, message = "Job cannot exceed 100 characters")
    private String job;
    
    @Size(max = 100, message = "Education cannot exceed 100 characters")
    private String education;
    
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;
    
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;
    
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;
}