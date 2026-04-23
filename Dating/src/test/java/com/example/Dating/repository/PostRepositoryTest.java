package com.example.Dating.repository;

import com.example.Dating.entities.Post;
import com.example.Dating.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepository postRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().username("poster").email("post@x.com").password("pwd").build();
        entityManager.persist(user);
        Post post1 = Post.builder().user(user).content("First post").build();
        Post post2 = Post.builder().user(user).content("Second post").build();
        entityManager.persist(post1);
        entityManager.persist(post2);
        entityManager.flush();
    }

    @Test
    void findByUserUserIdOrderByCreatedAtDesc_ShouldReturnUserPosts() {
        var page = postRepository.findByUserUserIdOrderByCreatedAtDesc(user.getUserId(), PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void findAllByOrderByCreatedAtDesc_ShouldReturnAllPosts() {
        var page = postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(2);
    }
}