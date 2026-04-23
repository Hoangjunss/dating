package com.example.Dating.repository;

import com.example.Dating.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private TestEntityManager entityManager;
    @Autowired private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("encodedPass")
                .build();
        entityManager.persistAndFlush(user);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        Optional<User> found = userRepository.findByUsername("john_doe");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john_doe");
    }

    @Test
    void findByUsernameOrEmail_ShouldMatchUsername() {
        Optional<User> found = userRepository.findByUsernameOrEmail("john_doe");
        assertThat(found).isPresent();
    }

    @Test
    void existsByUsername_ShouldReturnTrue() {
        assertThat(userRepository.existsByUsername("john_doe")).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnTrue() {
        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
    }
}