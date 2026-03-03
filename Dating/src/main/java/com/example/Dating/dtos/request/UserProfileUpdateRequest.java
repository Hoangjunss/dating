package com.example.Dating.dtos.request;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

    private String displayName;
    private String bio;
    private String job;
    private String education;
    private String city;
    private Double latitude;
    private Double longitude;
}