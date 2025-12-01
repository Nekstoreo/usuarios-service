package com.pragma.usuarios.infrastructure.input.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.application.handler.IUserHandler;
import com.pragma.usuarios.domain.exception.InvalidEmailException;
import com.pragma.usuarios.domain.exception.UserAlreadyExistsException;
import com.pragma.usuarios.domain.exception.UserUnderageException;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    private static final String BASE_URL = "/api/v1/users";
    private static final String OWNER_EMAIL = "john.doe@email.com";
    private static final String OWNER_PHONE = "+573001234567";
    private static final String OWNER_DOCUMENT = "123456789";
    private static final String OWNERS_ENDPOINT = "/owners";
    private static final String DEFAULT_PASSWORD = "password123";
    private static final String MESSAGE_JSON_PATH = "$.message";

    @Mock
    private IUserHandler userHandler;

    @InjectMocks
    private UserRestController userRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("Create Owner - Success Cases")
    class CreateOwnerSuccessCases {

        @Test
        @DisplayName("Should create owner successfully and return 201")
        void shouldCreateOwnerSuccessfullyAndReturn201() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            UserResponse response = UserResponse.builder()
                    .id(1L)
                    .firstName("John")
                    .lastName("Doe")
                    .identityDocument(OWNER_DOCUMENT)
                    .phone(OWNER_PHONE)
                    .birthDate(LocalDate.of(1990, 5, 15))
                    .email(OWNER_EMAIL)
                    .role("OWNER")
                    .build();

            when(userHandler.createOwner(any(CreateOwnerRequest.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value(OWNER_EMAIL))
                    .andExpect(jsonPath("$.role").value("OWNER"));

            verify(userHandler).createOwner(any(CreateOwnerRequest.class));
        }
    }

    @Nested
    @DisplayName("Create Owner - Validation Errors")
    class CreateOwnerValidationErrors {

        @Test
        @DisplayName("Should return 400 when first name is blank")
        void shouldReturn400WhenFirstNameIsBlank() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userHandler, never()).createOwner(any());
        }

        @Test
        @DisplayName("Should return 400 when email is blank")
        void shouldReturn400WhenEmailIsBlank() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    "",
                    DEFAULT_PASSWORD
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userHandler, never()).createOwner(any());
        }

        @Test
        @DisplayName("Should return 400 when birth date is null")
        void shouldReturn400WhenBirthDateIsNull() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    null,
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userHandler, never()).createOwner(any());
        }

        @Test
        @DisplayName("Should return 400 when identity document is blank")
        void shouldReturn400WhenIdentityDocumentIsBlank() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    "",
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(userHandler, never()).createOwner(any());
        }
    }

    @Nested
    @DisplayName("Create Owner - Domain Exceptions")
    class CreateOwnerDomainExceptions {

        @Test
        @DisplayName("Should return 400 when email format is invalid")
        void shouldReturn400WhenEmailFormatIsInvalid() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    "invalid-email",
                    DEFAULT_PASSWORD
            );

            when(userHandler.createOwner(any(CreateOwnerRequest.class)))
                    .thenThrow(new InvalidEmailException("Invalid email format"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("Invalid email format"));
        }

        @Test
        @DisplayName("Should return 400 when user is underage")
        void shouldReturn400WhenUserIsUnderage() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.now().minusYears(17),
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            when(userHandler.createOwner(any(CreateOwnerRequest.class)))
                    .thenThrow(new UserUnderageException("User must be of legal age (18 years or older)"));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("User must be of legal age (18 years or older)"));
        }

        @Test
        @DisplayName("Should return 409 when user already exists")
        void shouldReturn409WhenUserAlreadyExists() throws Exception {
            // Arrange
            CreateOwnerRequest request = new CreateOwnerRequest(
                    "John",
                    "Doe",
                    OWNER_DOCUMENT,
                    OWNER_PHONE,
                    LocalDate.of(1990, 5, 15),
                    OWNER_EMAIL,
                    DEFAULT_PASSWORD
            );

            when(userHandler.createOwner(any(CreateOwnerRequest.class)))
                    .thenThrow(new UserAlreadyExistsException("A user already exists with email: " + OWNER_EMAIL));

            // Act & Assert
            mockMvc.perform(post(BASE_URL + OWNERS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath(MESSAGE_JSON_PATH).value("A user already exists with email: " + OWNER_EMAIL));
        }
    }

    @Nested
    @DisplayName("Get User By ID")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found by ID")
        void shouldReturnUserWhenFoundById() throws Exception {
            // Arrange
            Long userId = 1L;
            UserResponse response = UserResponse.builder()
                    .id(userId)
                    .firstName("John")
                    .lastName("Doe")
                    .identityDocument(OWNER_DOCUMENT)
                    .phone(OWNER_PHONE)
                    .birthDate(LocalDate.of(1990, 5, 15))
                    .email(OWNER_EMAIL)
                    .role("OWNER")
                    .build();

            when(userHandler.getUserById(userId)).thenReturn(Optional.of(response));

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(userId))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.email").value(OWNER_EMAIL))
                    .andExpect(jsonPath("$.role").value("OWNER"));

            verify(userHandler).getUserById(userId);
        }

        @Test
        @DisplayName("Should return 404 when user not found by ID")
        void shouldReturn404WhenUserNotFoundById() throws Exception {
            // Arrange
            Long userId = 999L;
            when(userHandler.getUserById(userId)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get(BASE_URL + "/" + userId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userHandler).getUserById(userId);
        }
    }
}
