package com.example.Dating.repository;

import com.example.Dating.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private Conversation conversation;

    @BeforeEach
    void setUp() {
        User u1 = User.builder().username("u1").email("u1@x.com").password("pwd").build();
        User u2 = User.builder().username("u2").email("u2@x.com").password("pwd").build();
        entityManager.persist(u1);
        entityManager.persist(u2);
        conversation = Conversation.builder().userA(u1).userB(u2).build();
        entityManager.persist(conversation);
        Message msg1 = Message.builder().conversation(conversation).sender(u1).type(MessageType.TEXT).content("Hi").build();
        Message msg2 = Message.builder().conversation(conversation).sender(u2).type(MessageType.TEXT).content("Hello").build();
        entityManager.persist(msg1);
        entityManager.persist(msg2);
        entityManager.flush();
    }

    @Test
    void findByConversationIdOrderBySentAtAsc_ShouldReturnMessagesInOrder() {
        List<Message> messages = messageRepository.findByConversationIdOrderBySentAtAsc(conversation.getId());
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Hi");
        assertThat(messages.get(1).getContent()).isEqualTo("Hello");
    }
}