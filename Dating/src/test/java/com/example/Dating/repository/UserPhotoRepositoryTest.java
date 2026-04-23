package com.example.Dating.repository;

import com.example.Dating.entities.User;
import com.example.Dating.entities.UserPhoto;
import com.example.Dating.entities.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;


import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserPhotoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserPhotoRepository userPhotoRepository;

    private User user;
    private UserProfile profile;
    private UserPhoto photo1, photo2;

    @BeforeEach
    void setUp() {
        user = User.builder().username("photoUser").email("photo@x.com").password("pwd").build();
        entityManager.persist(user);
        profile = UserProfile.builder().user(user).displayName("PhotoUser").build();
        entityManager.persist(profile);
        photo1 = UserPhoto.builder().userProfile(profile).url("url1").sortOrder(1).isPrimary(true).build();
        photo2 = UserPhoto.builder().userProfile(profile).url("url2").sortOrder(2).isPrimary(false).build();
        entityManager.persist(photo1);
        entityManager.persist(photo2);
        entityManager.flush();
    }

    @Test
    void findByUserProfile_User_UserId_ShouldReturnPhotos() {
        List<UserPhoto> photos = userPhotoRepository.findByUserProfile_User_UserId(user.getUserId());
        assertThat(photos).hasSize(2);
    }

    @Test
    void findFirstByUserProfile_User_UserIdAndIsPrimaryTrue_ShouldReturnPrimaryPhoto() {
        var primary = userPhotoRepository.findFirstByUserProfile_User_UserIdAndIsPrimaryTrue(user.getUserId());
        assertThat(primary).isPresent();
        assertThat(primary.get().getUrl()).isEqualTo("url1");
    }
}