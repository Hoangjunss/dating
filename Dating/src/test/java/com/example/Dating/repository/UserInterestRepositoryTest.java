package com.example.Dating.repository;

import com.example.Dating.entities.Interest;
import com.example.Dating.entities.User;
import com.example.Dating.entities.UserInterest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserInterestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserInterestRepository userInterestRepository;

    private User user;
    private Interest interest;

    @BeforeEach
    void setUp() {
        user = User.builder().username("userInt").email("int@x.com").password("pwd").build();
        interest = Interest.builder().name("Music").build();
        entityManager.persist(user);
        entityManager.persist(interest);
        UserInterest ui = UserInterest.builder().user(user).interest(interest).build();
        entityManager.persist(ui);
        entityManager.flush();
    }

    @Test
    void findByUser_UserId_ShouldReturnInterests() {
        List<UserInterest> list = userInterestRepository.findByUser_UserId(user.getUserId());
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getInterest().getName()).isEqualTo("Music");
    }

    @Test
    void existsByUser_UserIdAndInterest_Id_ShouldReturnTrue() {
        boolean exists = userInterestRepository.existsByUser_UserIdAndInterest_Id(user.getUserId(), interest.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void deleteByUserProfile_UserIdAndInterest_Id_ShouldDelete() {
        userInterestRepository.deleteByUserProfile_UserIdAndInterest_Id(user.getUserId(), interest.getId());
        boolean exists = userInterestRepository.existsByUser_UserIdAndInterest_Id(user.getUserId(), interest.getId());
        assertThat(exists).isFalse();
    }
}