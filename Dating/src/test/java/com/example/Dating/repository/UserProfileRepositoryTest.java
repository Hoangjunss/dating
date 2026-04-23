package com.example.Dating.repository;

import com.example.Dating.entities.User;
import com.example.Dating.entities.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User user;
    private UserProfile profile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("alice")
                .email("alice@example.com")
                .password("pwd")
                .build();
        entityManager.persist(user);
        profile = UserProfile.builder()
                .user(user)
                .displayName("Alice Wonder")
                .gender("FEMALE")
                .city("HCMC")
                .build();
        entityManager.persist(profile);
        entityManager.flush();
    }

    @Test
    void existsByDisplayName_ShouldReturnTrue_WhenExists() {
        boolean exists = userProfileRepository.existsByDisplayName("Alice Wonder");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByDisplayName_ShouldReturnFalse_WhenNotExists() {
        boolean exists = userProfileRepository.existsByDisplayName("Unknown");
        assertThat(exists).isFalse();
    }

    @Test
    void findFallbackCandidates_ShouldExcludeSelf() {
        var page = userProfileRepository.findFallbackCandidates(user.getUserId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).isEmpty();
    }
}