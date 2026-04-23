package com.example.Dating.repository;

import com.example.Dating.entities.User;
import com.example.Dating.entities.UserMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserMatchRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserMatchRepository matchRepository;

    private User userA, userB;
    private UserMatch match;

    @BeforeEach
    void setUp() {
        userA = User.builder().username("a").email("a@a.com").password("pwd").build();
        userB = User.builder().username("b").email("b@b.com").password("pwd").build();
        entityManager.persist(userA);
        entityManager.persist(userB);
        match = UserMatch.builder().userA(userA).userB(userB).active(true).build();
        entityManager.persist(match);
        entityManager.flush();
    }

    @Test
    void findActiveMatchesByUserId_ShouldReturnActiveMatch() {
        List<UserMatch> matches = matchRepository.findActiveMatchesByUserId(userA.getUserId());
        assertThat(matches).hasSize(1);
        assertThat(matches.get(0).getActive()).isTrue();
    }

    @Test
    void findByUserA_UserIdAndUserB_UserId_ShouldReturnMatch() {
        Optional<UserMatch> found = matchRepository.findByUserA_UserIdAndUserB_UserId(userA.getUserId(), userB.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getActive()).isTrue();
    }

    @Test
    void existsByUserA_UserIdAndUserB_UserIdAndActiveTrue_ShouldReturnTrue() {
        boolean exists = matchRepository.existsByUserA_UserIdAndUserB_UserIdAndActiveTrue(userA.getUserId(), userB.getUserId());
        assertThat(exists).isTrue();
    }

    @Test
    void findAllByUserId_ShouldReturnAllMatches() {
        List<UserMatch> matches = matchRepository.findAllByUserId(userA.getUserId());
        assertThat(matches).hasSize(1);
    }
}