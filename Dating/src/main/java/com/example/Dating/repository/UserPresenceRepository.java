package com.example.Dating.repository;

import com.example.Dating.entities.UserPresence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserPresenceRepository extends JpaRepository<UserPresence, UUID> {
    List<UserPresence> findByUserIdIn(List<UUID> userIds);
}