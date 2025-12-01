package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.CreateEmployeeRequest;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.application.mapper.UserRequestMapper;
import com.pragma.usuarios.application.mapper.UserResponseMapper;
import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    private static final String OWNER_EMAIL = "juan.perez@email.com";
    private static final String OWNER_PHONE = "+573001234567";
    private static final String OWNER_ROLE = "OWNER";
    private static final String EMPLOYEE_ROLE = "EMPLOYEE";
    private static final String OWNER_LAST_NAME = "Pérez";
    private static final String OWNER_IDENTITY_DOCUMENT = "123456789";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final Long RESTAURANT_ID = 1L;

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

    @Nested
    @DisplayName("Create Owner Tests")
    class CreateOwnerTests {

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

    @Nested
    @DisplayName("Create Employee Tests")
    class CreateEmployeeTests {

        private CreateEmployeeRequest createEmployeeRequest;
        private User employeeUser;
        private User savedEmployeeUser;
        private UserResponse employeeResponse;

        @BeforeEach
        void setUp() {
            createEmployeeRequest = new CreateEmployeeRequest(
                    "Carlos",
                    "García",
                    "987654321",
                    "+573009876543",
                    "carlos.garcia@email.com",
                    "employee123",
                    RESTAURANT_ID
            );

            employeeUser = new User();
            employeeUser.setFirstName("Carlos");
            employeeUser.setLastName("García");
            employeeUser.setIdentityDocument("987654321");
            employeeUser.setPhone("+573009876543");
            employeeUser.setEmail("carlos.garcia@email.com");
            employeeUser.setPassword("employee123");
            employeeUser.setRestaurantId(RESTAURANT_ID);

            Role employeeRole = new Role(3L, EMPLOYEE_ROLE, "Restaurant employee");

            savedEmployeeUser = new User();
            savedEmployeeUser.setId(2L);
            savedEmployeeUser.setFirstName("Carlos");
            savedEmployeeUser.setLastName("García");
            savedEmployeeUser.setIdentityDocument("987654321");
            savedEmployeeUser.setPhone("+573009876543");
            savedEmployeeUser.setEmail("carlos.garcia@email.com");
            savedEmployeeUser.setPassword(ENCODED_PASSWORD);
            savedEmployeeUser.setRole(employeeRole);
            savedEmployeeUser.setRestaurantId(RESTAURANT_ID);

            employeeResponse = UserResponse.builder()
                    .id(2L)
                    .firstName("Carlos")
                    .lastName("García")
                    .identityDocument("987654321")
                    .phone("+573009876543")
                    .email("carlos.garcia@email.com")
                    .role(EMPLOYEE_ROLE)
                    .build();
        }

        @Test
        @DisplayName("Should create employee and return response")
        void shouldCreateEmployeeAndReturnResponse() {
            // Arrange
            when(userRequestMapper.toUser(createEmployeeRequest)).thenReturn(employeeUser);
            when(userServicePort.createEmployee(employeeUser)).thenReturn(savedEmployeeUser);
            when(userResponseMapper.toResponse(savedEmployeeUser)).thenReturn(employeeResponse);

            // Act
            UserResponse result = userHandler.createEmployee(createEmployeeRequest);

            // Assert
            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("Carlos", result.getFirstName());
            assertEquals("García", result.getLastName());
            assertEquals(EMPLOYEE_ROLE, result.getRole());

            verify(userRequestMapper).toUser(createEmployeeRequest);
            verify(userServicePort).createEmployee(employeeUser);
            verify(userResponseMapper).toResponse(savedEmployeeUser);
        }

        @Test
        @DisplayName("Should call mapper to convert request to domain model for employee")
        void shouldCallMapperToConvertRequestToDomainModelForEmployee() {
            // Arrange
            when(userRequestMapper.toUser(any(CreateEmployeeRequest.class))).thenReturn(employeeUser);
            when(userServicePort.createEmployee(any(User.class))).thenReturn(savedEmployeeUser);
            when(userResponseMapper.toResponse(any(User.class))).thenReturn(employeeResponse);

            // Act
            userHandler.createEmployee(createEmployeeRequest);

            // Assert
            verify(userRequestMapper).toUser(createEmployeeRequest);
        }

        @Test
        @DisplayName("Should call service port to create employee")
        void shouldCallServicePortToCreateEmployee() {
            // Arrange
            when(userRequestMapper.toUser(any(CreateEmployeeRequest.class))).thenReturn(employeeUser);
            when(userServicePort.createEmployee(any(User.class))).thenReturn(savedEmployeeUser);
            when(userResponseMapper.toResponse(any(User.class))).thenReturn(employeeResponse);

            // Act
            userHandler.createEmployee(createEmployeeRequest);

            // Assert
            verify(userServicePort).createEmployee(employeeUser);
        }
    }

    @Nested
    @DisplayName("Get User By ID Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user response when user exists")
        void shouldReturnUserResponseWhenUserExists() {
            // Arrange
            Long userId = 1L;
            when(userServicePort.getUserById(userId)).thenReturn(Optional.of(savedUser));
            when(userResponseMapper.toResponse(savedUser)).thenReturn(userResponse);

            // Act
            Optional<UserResponse> result = userHandler.getUserById(userId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(userId, result.get().getId());
            assertEquals(OWNER_EMAIL, result.get().getEmail());
            verify(userServicePort).getUserById(userId);
            verify(userResponseMapper).toResponse(savedUser);
        }

        @Test
        @DisplayName("Should return empty when user does not exist")
        void shouldReturnEmptyWhenUserDoesNotExist() {
            // Arrange
            Long userId = 999L;
            when(userServicePort.getUserById(userId)).thenReturn(Optional.empty());

            // Act
            Optional<UserResponse> result = userHandler.getUserById(userId);

            // Assert
            assertTrue(result.isEmpty());
            verify(userServicePort).getUserById(userId);
        }
    }
}
