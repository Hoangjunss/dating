package com.example.Dating.repository;

import com.example.Dating.entities.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserPreferenceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPreferenceRepository preferenceRepository;

    private UUID userId;
    private UserPreference preference;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        preference = UserPreference.builder()
                .userId(userId)
                .genderPreference("FEMALE")
                .minAge(20)
                .maxAge(30)
                .maxDistanceKm(50)
                .build();
        entityManager.persist(preference);
        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnPreference() {
        Optional<UserPreference> found = preferenceRepository.findById(userId);
        assertThat(found).isPresent();
        assertThat(found.get().getGenderPreference()).isEqualTo("FEMALE");
    }
}