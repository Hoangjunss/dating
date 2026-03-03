package com.example.Dating.repository;

import com.example.Dating.entities.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserPreferenceRepository
        extends JpaRepository<UserPreference, UUID> {}