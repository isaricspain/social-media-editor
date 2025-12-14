package com.socialmedia.editor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmedia.editor.model.User;
import com.socialmedia.editor.repository.UserRepository;
import com.socialmedia.editor.service.AuthService;
import com.socialmedia.editor.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    void fullAuthenticationFlow_RegisterThenLogin_ShouldWork() throws Exception {
        AuthController.RegisterRequest registerRequest = TestDataBuilder.aRegisterRequest()
                .withUsername("integrationuser")
                .withEmail("integration@example.com")
                .withPassword("integration123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        AuthController.LoginRequest loginRequest = TestDataBuilder.aLoginRequest()
                .withUsername("integrationuser")
                .withPassword("integration123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(notNullValue())))
                .andExpect(jsonPath("$.username", is("integrationuser")));
    }

    @Test
    void register_WithRealDatabase_ShouldPersistUser() throws Exception {
        AuthController.RegisterRequest registerRequest = TestDataBuilder.aRegisterRequest()
                .withUsername("persisteduser")
                .withEmail("persisted@example.com")
                .withPassword("persisted123")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        User savedUser = userRepository.findByUsername("persisteduser").orElse(null);
        assert savedUser != null;
        assert savedUser.getUsername().equals("persisteduser");
        assert savedUser.getEmail().equals("persisted@example.com");
        assert TestDataBuilder.matchesPassword("persisted123", savedUser.getPassword());
    }

    @Test
    void login_WithExistingUser_ShouldReturnValidJwtToken() throws Exception {
        User user = authService.registerUser("existinguser", "existing@example.com", "existing123");

        AuthController.LoginRequest loginRequest = TestDataBuilder.aLoginRequest()
                .withUsername("existinguser")
                .withPassword("existing123")
                .build();

        String responseContent = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(notNullValue())))
                .andExpect(jsonPath("$.username", is("existinguser")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        @SuppressWarnings("unchecked")
        var responseMap = objectMapper.readValue(responseContent, java.util.Map.class);
        String token = (String) responseMap.get("token");

        assert authService.validateJwtToken(token);
        assert authService.getUsernameFromJwtToken(token).equals("existinguser");
    }

    @Test
    void register_DuplicateUsername_ShouldReturnError() throws Exception {
        authService.registerUser("duplicateuser", "first@example.com", "password123");

        AuthController.RegisterRequest registerRequest = TestDataBuilder.aRegisterRequest()
                .withUsername("duplicateuser")
                .withEmail("second@example.com")
                .withPassword("password456")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken!"));
    }

    @Test
    void register_DuplicateEmail_ShouldReturnError() throws Exception {
        authService.registerUser("firstuser", "duplicate@example.com", "password123");

        AuthController.RegisterRequest registerRequest = TestDataBuilder.aRegisterRequest()
                .withUsername("seconduser")
                .withEmail("duplicate@example.com")
                .withPassword("password456")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already in use!"));
    }

    @Test
    void login_WithWrongPassword_ShouldReturnError() throws Exception {
        authService.registerUser("testuser", "test@example.com", "correctpassword");

        AuthController.LoginRequest loginRequest = TestDataBuilder.aLoginRequest()
                .withUsername("testuser")
                .withPassword("wrongpassword")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnError() throws Exception {
        AuthController.LoginRequest loginRequest = TestDataBuilder.aLoginRequest()
                .withUsername("nonexistentuser")
                .withPassword("password123")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid username or password"));
    }
}