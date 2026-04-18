package com.example.Dating.repository;

import com.example.Dating.entities.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, UUID> {

    List<UserPhoto> findByUserProfile_User_UserId(UUID userId);

    List<UserPhoto> findByUserProfile_User_UserIdIn(List<UUID> userIds);

    Optional<UserPhoto> findFirstByUserProfile_User_UserIdAndIsPrimaryTrue(UUID userId);
}