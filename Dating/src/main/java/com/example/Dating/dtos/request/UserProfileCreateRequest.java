package com.example.Dating.dtos.request;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UserProfileCreateRequest {

    private UUID userId;
    private String displayName;
    private String gender;
    private LocalDate birthday;
    private String bio;
    private Integer heightCm;
    private String job;
    private String education;
    private String city;
    private Double latitude;
    private Double longitude;
}