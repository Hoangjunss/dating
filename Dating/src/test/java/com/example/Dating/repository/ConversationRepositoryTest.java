package com.example.Dating.repository;

import com.example.Dating.entities.Conversation;
import com.example.Dating.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConversationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ConversationRepository conversationRepository;

    private User userA, userB;
    private Conversation conversation;

    @BeforeEach
    void setUp() {
        userA = User.builder().username("a").email("a@a.com").password("pwd").build();
        userB = User.builder().username("b").email("b@b.com").password("pwd").build();
        entityManager.persist(userA);
        entityManager.persist(userB);
        conversation = Conversation.builder().userA(userA).userB(userB).build();
        entityManager.persist(conversation);
        entityManager.flush();
    }

    @Test
    void findByUserAUserIdAndUserBUserId_ShouldReturnConversation() {
        Optional<Conversation> found = conversationRepository.findByUserAUserIdAndUserBUserId(userA.getUserId(), userB.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(conversation.getId());
    }

    @Test
    void findByUserAUserIdOrUserBUserId_ShouldReturnConversationsForUser() {
        List<Conversation> list = conversationRepository.findByUserAUserIdOrUserBUserId(userA.getUserId(), userA.getUserId());
        assertThat(list).hasSize(1);
    }
}