package com.example.Dating.repository;

import com.example.Dating.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository
        extends JpaRepository<Conversation, UUID>, JpaSpecificationExecutor<Conversation> {

    Optional<Conversation> findByUserAUserIdAndUserBUserId(UUID userAId, UUID userBId);


    List<Conversation> findByUserAUserIdOrUserBUserId(UUID a, UUID b);

    Optional<Conversation> findByUserA_UserIdAndUserB_UserId(UUID a, UUID b);


}