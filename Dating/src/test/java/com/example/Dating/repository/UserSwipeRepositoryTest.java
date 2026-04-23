package com.example.Dating.repository;


import com.example.Dating.entities.User;
import com.example.Dating.entities.UserSwipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserSwipeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserSwipeRepository swipeRepository;

    private User fromUser, toUser;

    @BeforeEach
    void setUp() {
        fromUser = User.builder().username("swiper").email("swiper@x.com").password("pwd").build();
        toUser = User.builder().username("target").email("target@x.com").password("pwd").build();
        entityManager.persist(fromUser);
        entityManager.persist(toUser);
        entityManager.flush();
    }

    @Test
    void existsByFromUser_UserIdAndToUser_UserId_ShouldReturnTrue_AfterSwipe() {
        UserSwipe swipe = UserSwipe.builder().fromUser(fromUser).toUser(toUser).isLiked(true).build();
        entityManager.persist(swipe);
        entityManager.flush();

        boolean exists = swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromUser.getUserId(), toUser.getUserId());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByFromUser_UserIdAndToUser_UserId_ShouldReturnFalse_WhenNoSwipe() {
        boolean exists = swipeRepository.existsByFromUser_UserIdAndToUser_UserId(fromUser.getUserId(), toUser.getUserId());
        assertThat(exists).isFalse();
    }

    @Test
    void findFromUserIdSet_ShouldReturnSwipedToUserIds() {
        UserSwipe swipe1 = UserSwipe.builder().fromUser(fromUser).toUser(toUser).isLiked(true).build();
        entityManager.persist(swipe1);
        entityManager.flush();

        Set<UUID> swipedIds = swipeRepository.findFromUserIdSet(fromUser.getUserId());
        assertThat(swipedIds).containsExactly(toUser.getUserId());
    }

    @Test
    void existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue_ShouldReturnTrue_WhenLikeExists() {
        UserSwipe swipe = UserSwipe.builder().fromUser(fromUser).toUser(toUser).isLiked(true).build();
        entityManager.persist(swipe);
        entityManager.flush();

        boolean exists = swipeRepository.existsByFromUser_UserIdAndToUser_UserIdAndIsLikedTrue(fromUser.getUserId(), toUser.getUserId());
        assertThat(exists).isTrue();
    }
}