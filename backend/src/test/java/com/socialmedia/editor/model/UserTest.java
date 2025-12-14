package com.socialmedia.editor.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createUser_WithValidData_ShouldPassValidation() {
        User user = new User("validuser", "valid@example.com", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void createUser_WithBlankUsername_ShouldFailValidation() {
        User user = new User("", "valid@example.com", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(2); // NotBlank and Size validation
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")))
                .isTrue();
    }

    @Test
    void createUser_WithNullUsername_ShouldFailValidation() {
        User user = new User(null, "valid@example.com", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("username");
    }

    @Test
    void createUser_WithShortUsername_ShouldFailValidation() {
        User user = new User("ab", "valid@example.com", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("username");
    }

    @Test
    void createUser_WithLongUsername_ShouldFailValidation() {
        User user = new User("a".repeat(21), "valid@example.com", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("username");
    }

    @Test
    void createUser_WithBlankEmail_ShouldFailValidation() {
        User user = new User("validuser", "", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1); // Only NotBlank since empty string is invalid email format
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void createUser_WithInvalidEmailFormat_ShouldFailValidation() {
        User user = new User("validuser", "invalidemail", "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void createUser_WithLongEmail_ShouldFailValidation() {
        String longEmail = "a".repeat(45) + "@example.com"; // Over 50 characters
        User user = new User("validuser", longEmail, "password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("email");
    }

    @Test
    void createUser_WithBlankPassword_ShouldFailValidation() {
        User user = new User("validuser", "valid@example.com", "");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(2); // NotBlank and Size validation
        assertThat(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")))
                .isTrue();
    }

    @Test
    void createUser_WithShortPassword_ShouldFailValidation() {
        User user = new User("validuser", "valid@example.com", "12345");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    void createUser_WithLongPassword_ShouldFailValidation() {
        String longPassword = "a".repeat(121); // Over 120 characters
        User user = new User("validuser", "valid@example.com", longPassword);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("password");
    }

    @Test
    void createUser_WithMinimumValidLength_ShouldPassValidation() {
        User user = new User("abc", "a@b.co", "123456");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void createUser_WithMaximumValidLength_ShouldPassValidation() {
        String maxUsername = "a".repeat(20);
        String maxEmail = "a".repeat(41) + "@test.com"; // 50 characters total
        String maxPassword = "a".repeat(120);
        User user = new User(maxUsername, maxEmail, maxPassword);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isEmpty();
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyUser() {
        User user = new User();

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getPassword()).isNull();
    }
}