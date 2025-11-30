package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.application.mapper.UserRequestMapper;
import com.pragma.usuarios.application.mapper.UserResponseMapper;
import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    private static final String OWNER_EMAIL = "juan.perez@email.com";
    private static final String OWNER_PHONE = "+573001234567";
    private static final String OWNER_ROLE = "OWNER";
    private static final String OWNER_LAST_NAME = "Pérez";
    private static final String OWNER_IDENTITY_DOCUMENT = "123456789";
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private UserRequestMapper userRequestMapper;

    @Mock
    private UserResponseMapper userResponseMapper;

    @InjectMocks
    private UserHandler userHandler;

    private CreateOwnerRequest createOwnerRequest;
    private User user;
    private User savedUser;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createOwnerRequest = new CreateOwnerRequest(
                "Juan",
                OWNER_LAST_NAME,
                OWNER_IDENTITY_DOCUMENT,
                OWNER_PHONE,
                LocalDate.of(1990, 5, 15),
                OWNER_EMAIL,
                "secret123"
        );

        user = new User();
        user.setFirstName("Juan");
        user.setLastName(OWNER_LAST_NAME);
        user.setIdentityDocument(OWNER_IDENTITY_DOCUMENT);
        user.setPhone(OWNER_PHONE);
        user.setBirthDate(LocalDate.of(1990, 5, 15));
        user.setEmail(OWNER_EMAIL);
        user.setPassword("secret123");

        Role ownerRole = new Role(1L, OWNER_ROLE, "Restaurant owner");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Juan");
        savedUser.setLastName(OWNER_LAST_NAME);
        savedUser.setIdentityDocument(OWNER_IDENTITY_DOCUMENT);
        savedUser.setPhone(OWNER_PHONE);
        savedUser.setBirthDate(LocalDate.of(1990, 5, 15));
        savedUser.setEmail(OWNER_EMAIL);
        savedUser.setPassword(ENCODED_PASSWORD);
        savedUser.setRole(ownerRole);

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("Juan")
                .lastName(OWNER_LAST_NAME)
                .identityDocument(OWNER_IDENTITY_DOCUMENT)
                .phone(OWNER_PHONE)
                .birthDate(LocalDate.of(1990, 5, 15))
                .email(OWNER_EMAIL)
                .role(OWNER_ROLE)
                .build();
    }

    @Test
    @DisplayName("Should create owner and return response")
    void shouldCreateOwnerAndReturnResponse() {
        // Arrange
        when(userRequestMapper.toUser(createOwnerRequest)).thenReturn(user);
        when(userServicePort.createOwner(user)).thenReturn(savedUser);
        when(userResponseMapper.toResponse(savedUser)).thenReturn(userResponse);

        // Act
        UserResponse result = userHandler.createOwner(createOwnerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getFirstName());
        assertEquals(OWNER_LAST_NAME, result.getLastName());
        assertEquals(OWNER_ROLE, result.getRole());

        verify(userRequestMapper).toUser(createOwnerRequest);
        verify(userServicePort).createOwner(user);
        verify(userResponseMapper).toResponse(savedUser);
    }

    @Test
    @DisplayName("Should call mapper to convert request to domain model")
    void shouldCallMapperToConvertRequestToDomainModel() {
        // Arrange
        when(userRequestMapper.toUser(any(CreateOwnerRequest.class))).thenReturn(user);
        when(userServicePort.createOwner(any(User.class))).thenReturn(savedUser);
        when(userResponseMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // Act
        userHandler.createOwner(createOwnerRequest);

        // Assert
        verify(userRequestMapper).toUser(createOwnerRequest);
    }

    @Test
    @DisplayName("Should call service port to create owner")
    void shouldCallServicePortToCreateOwner() {
        // Arrange
        when(userRequestMapper.toUser(any(CreateOwnerRequest.class))).thenReturn(user);
        when(userServicePort.createOwner(any(User.class))).thenReturn(savedUser);
        when(userResponseMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // Act
        userHandler.createOwner(createOwnerRequest);

        // Assert
        verify(userServicePort).createOwner(user);
    }

    @Test
    @DisplayName("Should call mapper to convert saved user to response")
    void shouldCallMapperToConvertSavedUserToResponse() {
        // Arrange
        when(userRequestMapper.toUser(any(CreateOwnerRequest.class))).thenReturn(user);
        when(userServicePort.createOwner(any(User.class))).thenReturn(savedUser);
        when(userResponseMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // Act
        userHandler.createOwner(createOwnerRequest);

        // Assert
        verify(userResponseMapper).toResponse(savedUser);
    }
}
