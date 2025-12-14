package com.socialmedia.editor.util;

import com.socialmedia.editor.controller.AuthController;
import com.socialmedia.editor.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestDataBuilder {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static class UserBuilder {
        private String username = "testuser";
        private String email = "test@example.com";
        private String password = "password123";
        private boolean encodePassword = false;

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder withEncodedPassword() {
            this.encodePassword = true;
            return this;
        }

        public User build() {
            String finalPassword = encodePassword ? passwordEncoder.encode(password) : password;
            return new User(username, email, finalPassword);
        }
    }

    public static class LoginRequestBuilder {
        private String username = "testuser";
        private String password = "password123";

        public LoginRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public LoginRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public AuthController.LoginRequest build() {
            AuthController.LoginRequest request = new AuthController.LoginRequest();
            request.setUsername(username);
            request.setPassword(password);
            return request;
        }
    }

    public static class RegisterRequestBuilder {
        private String username = "newuser";
        private String email = "newuser@example.com";
        private String password = "password123";

        public RegisterRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public RegisterRequestBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public AuthController.RegisterRequest build() {
            AuthController.RegisterRequest request = new AuthController.RegisterRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setPassword(password);
            return request;
        }
    }

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public static LoginRequestBuilder aLoginRequest() {
        return new LoginRequestBuilder();
    }

    public static RegisterRequestBuilder aRegisterRequest() {
        return new RegisterRequestBuilder();
    }

    public static User createValidUser() {
        return aUser().build();
    }

    public static User createUserWithEncodedPassword() {
        return aUser().withEncodedPassword().build();
    }

    public static User createUserWithCustomData(String username, String email, String password) {
        return aUser()
                .withUsername(username)
                .withEmail(email)
                .withPassword(password)
                .build();
    }

    public static AuthController.LoginRequest createValidLoginRequest() {
        return aLoginRequest().build();
    }

    public static AuthController.RegisterRequest createValidRegisterRequest() {
        return aRegisterRequest().build();
    }

    public static String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}