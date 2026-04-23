package com.example.Dating.repository;

import com.example.Dating.entities.UserPresence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserPresenceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPresenceRepository userPresenceRepository;

    private UUID userId1, userId2;

    @BeforeEach
    void setUp() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        UserPresence p1 = UserPresence.builder().userId(userId1).online(true).build();
        UserPresence p2 = UserPresence.builder().userId(userId2).online(false).build();
        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();
    }

    @Test
    void findByUserIdIn_ShouldReturnPresencesForGivenIds() {
        List<UserPresence> list = userPresenceRepository.findByUserIdIn(List.of(userId1, userId2));
        assertThat(list).hasSize(2);
    }
}