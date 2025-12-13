package com.socialmedia.editor.repository;

import com.socialmedia.editor.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        User user = new User("testuser", "test@example.com", "password123");
        entityManager.persistAndFlush(user);

        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isNotPresent();
    }

    @Test
    void existsByUsername_WhenUserExists_ShouldReturnTrue() {
        User user = new User("existinguser", "existing@example.com", "password123");
        entityManager.persistAndFlush(user);

        Boolean exists = userRepository.existsByUsername("existinguser");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByUsername_WhenUserDoesNotExist_ShouldReturnFalse() {
        Boolean exists = userRepository.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        User user = new User("testuser", "existing@example.com", "password123");
        entityManager.persistAndFlush(user);

        Boolean exists = userRepository.existsByEmail("existing@example.com");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        Boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertThat(exists).isFalse();
    }

    @Test
    void save_ShouldPersistUser() {
        User user = new User("newuser", "new@example.com", "password123");

        User saved = userRepository.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getPassword()).isEqualTo("password123");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        Iterable<User> users = userRepository.findAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        User user = new User("userToDelete", "delete@example.com", "password123");
        User saved = entityManager.persistAndFlush(user);
        Long userId = saved.getId();

        userRepository.deleteById(userId);

        Optional<User> found = userRepository.findById(userId);
        assertThat(found).isNotPresent();
    }
}