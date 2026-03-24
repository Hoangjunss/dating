package com.example.Dating.repository;

import com.example.Dating.entities.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserInterestRepository
        extends JpaRepository<UserInterest, UUID> {

    List<UserInterest> findAllByUserProfile_UserId(UUID userId);

    boolean existsByUser_UserIdAndInterest_Id(UUID userId, UUID interestId);

    void deleteByUserProfile_UserIdAndInterest_Id(UUID userId, UUID interestId);

    List<UserInterest> findByUser_UserId(UUID userId);

     //batch load interests for a pool of users
    List<UserInterest> findByUser_UserIdIn(List<UUID> userIds);
}