package com.example.Dating.repository;

import com.example.Dating.entities.Interest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InterestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InterestRepository interestRepository;

    @BeforeEach
    void setUp() {
        Interest interest = Interest.builder().name("Reading").build();
        entityManager.persist(interest);
        entityManager.flush();
    }

    @Test
    void existsByName_ShouldReturnTrue_WhenExists() {
        boolean exists = interestRepository.existsByName("Reading");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_ShouldReturnFalse_WhenNotExists() {
        boolean exists = interestRepository.existsByName("Cooking");
        assertThat(exists).isFalse();
    }
}