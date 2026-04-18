package com.example.Dating.repository;


import com.example.Dating.entities.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID>, JpaSpecificationExecutor<UserProfile> {

    boolean existsByDisplayName(String displayName);

    @Query("SELECT up FROM UserProfile up WHERE up.user.userId != :userId")
    Page<UserProfile> findFallbackCandidates(@Param("userId") UUID userId, Pageable pageable);

}