package com.example.Dating.repository;

import com.example.Dating.entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, UUID> {
    List<UserPhoto> findByUserId(UUID userId);
}