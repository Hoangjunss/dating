package com.example.Dating.repository;


import com.example.Dating.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    boolean existsByDisplayName(String displayName);

}