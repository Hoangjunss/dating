package com.example.Dating.dtos.response;



import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID userId;
    private String displayName;
    private String gender;
    private LocalDate birthday;
    private String bio;
    private String heightCm;
    private String job;
    private String education;
    private String city;
    private Boolean verified;
}