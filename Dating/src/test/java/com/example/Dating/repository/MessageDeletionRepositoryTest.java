package com.example.Dating.repository;


import com.example.Dating.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageDeletionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageDeletionRepository deletionRepository;

    private UUID userId;
    private Message message;

    @BeforeEach
    void setUp() {
        User user = User.builder().username("del").email("del@x.com").password("pwd").build();
        User other = User.builder().username("other").email("other@x.com").password("pwd").build();
        entityManager.persist(user);
        entityManager.persist(other);
        userId = user.getUserId();
        Conversation conv = Conversation.builder().userA(user).userB(other).build();
        entityManager.persist(conv);
        message = Message.builder().conversation(conv).sender(user).type(MessageType.TEXT).content("secret").build();
        entityManager.persist(message);
        MessageDeletion deletion = MessageDeletion.builder().message(message).userId(userId).build();
        entityManager.persist(deletion);
        entityManager.flush();
    }

    @Test
    void existsByMessage_IdAndUserId_ShouldReturnTrue() {
        boolean exists = deletionRepository.existsByMessage_IdAndUserId(message.getId(), userId);
        assertThat(exists).isTrue();
    }

    @Test
    void findDeletedMessageIdsByUserInConversation_ShouldReturnDeletedIds() {
        Set<UUID> deletedIds = deletionRepository.findDeletedMessageIdsByUserInConversation(userId, message.getConversation().getId());
        assertThat(deletedIds).containsExactly(message.getId());
    }
}