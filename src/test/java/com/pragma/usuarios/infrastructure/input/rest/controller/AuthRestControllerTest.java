package com.pragma.usuarios.infrastructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.usuarios.application.dto.request.LoginRequest;
import com.pragma.usuarios.application.dto.response.AuthResponse;
import com.pragma.usuarios.application.handler.IAuthHandler;
import com.pragma.usuarios.domain.exception.InvalidCredentialsException;
import com.pragma.usuarios.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthHandler authHandler;

    @InjectMocks
    private AuthRestController authRestController;

    private ObjectMapper objectMapper;

    private static final String AUTH_URL = "/api/v1/auth/login";
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String TOKEN = "jwt.token.here";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("Login - Success Cases")
    class LoginSuccessCases {

        @Test
        @DisplayName("Should return 200 and auth response when login is successful")
        void shouldReturn200WhenLoginIsSuccessful() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password(PASSWORD)
                    .build();

            AuthResponse response = AuthResponse.builder()
                    .token(TOKEN)
                    .tokenType("Bearer")
                    .userId(1L)
                    .email(EMAIL)
                    .role("OWNER")
                    .build();

            when(authHandler.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post(AUTH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value(TOKEN))
                    .andExpect(jsonPath("$.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.email").value(EMAIL))
                    .andExpect(jsonPath("$.role").value("OWNER"));
        }
    }

    @Nested
    @DisplayName("Login - Validation Errors")
    class LoginValidationErrors {

        @Test
        @DisplayName("Should return 400 when email is blank")
        void shouldReturn400WhenEmailIsBlank() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email("")
                    .password(PASSWORD)
                    .build();

            mockMvc.perform(post(AUTH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when email is invalid")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email("invalid-email")
                    .password(PASSWORD)
                    .build();

            mockMvc.perform(post(AUTH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when password is blank")
        void shouldReturn400WhenPasswordIsBlank() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("")
                    .build();

            mockMvc.perform(post(AUTH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Login - Authentication Errors")
    class LoginAuthenticationErrors {

        @Test
        @DisplayName("Should return 401 when credentials are invalid")
        void shouldReturn401WhenCredentialsAreInvalid() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .email(EMAIL)
                    .password("wrong-password")
                    .build();

            when(authHandler.login(any(LoginRequest.class)))
                    .thenThrow(new InvalidCredentialsException());

            mockMvc.perform(post(AUTH_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }
    }
}
