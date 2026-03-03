package com.example.Dating.entities;



import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private UUID userId;

    @Column(nullable = false)
    private String displayName;

    private String gender;

    private LocalDate birthday;

    @Column(length = 500)
    private String bio;

    private Integer heightCm;

    private String job;

    private String education;

    private String city;

    private Double latitude;

    private Double longitude;

    private Boolean verified;

    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        verified = false;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}