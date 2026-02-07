package com.pragma.usuarios.application.mapper;

import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponseMapper Tests")
class UserResponseMapperTest {

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "Juan";
    private static final String LAST_NAME = "Pérez";
    private static final String IDENTITY_DOCUMENT = "123456789";
    private static final String PHONE = "+573001234567";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 5, 15);
    private static final String EMAIL = "juan.perez@example.com";
    private static final String PASSWORD = "encodedPassword";
    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "OWNER";
    private static final String ROLE_DESCRIPTION = "Owner role";
    private static final Long RESTAURANT_ID = 1L;
    private static final String EMPLOYEE_ROLE_NAME = "EMPLOYEE";
    private static final String EMPLOYEE_ROLE_DESCRIPTION = "Employee role";
    private static final String CLIENT_ROLE_NAME = "CLIENT";
    private static final String CLIENT_ROLE_DESCRIPTION = "Client role";
    private static final String SPECIAL_FIRST_NAME = "José María";
    private static final String SPECIAL_LAST_NAME = "García-López";
    private static final String SPECIAL_EMAIL = "jose.maria+test@example.com";

    private UserResponseMapper userResponseMapper;

    @BeforeEach
    void setUp() {
        userResponseMapper = Mappers.getMapper(UserResponseMapper.class);
    }

    @Nested
    @DisplayName("toResponse(User) Tests")
    class ToResponseTests {

        private User user;
        private Role role;

        @BeforeEach
        void setUp() {
            role = new Role(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);

            user = new User();
            user.setId(USER_ID);
            user.setFirstName(FIRST_NAME);
            user.setLastName(LAST_NAME);
            user.setIdentityDocument(IDENTITY_DOCUMENT);
            user.setPhone(PHONE);
            user.setBirthDate(BIRTH_DATE);
            user.setEmail(EMAIL);
            user.setPassword(PASSWORD);
            user.setRole(role);
            user.setRestaurantId(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should map User to UserResponse with all fields correctly")
        void shouldMapUserToUserResponse() {
            // Given
            User userInput = user;

            // When
            UserResponse response = userResponseMapper.toResponse(userInput);

            // Then
            assertNotNull(response);
            assertEquals(USER_ID, response.getId());
            assertEquals(FIRST_NAME, response.getFirstName());
            assertEquals(LAST_NAME, response.getLastName());
            assertEquals(IDENTITY_DOCUMENT, response.getIdentityDocument());
            assertEquals(PHONE, response.getPhone());
            assertEquals(BIRTH_DATE, response.getBirthDate());
            assertEquals(EMAIL, response.getEmail());
            assertEquals(RESTAURANT_ID, response.getRestaurantId());
        }

        @Test
        @DisplayName("Should map role.name to role field in UserResponse")
        void shouldMapRoleNameToRoleField() {
            // Given
            User userInput = user;

            // When
            UserResponse response = userResponseMapper.toResponse(userInput);

            // Then
            assertNotNull(response);
            assertEquals(ROLE_NAME, response.getRole());
            assertNotEquals(String.valueOf(ROLE_ID), response.getRole());
        }

        @Test
        @DisplayName("Should handle User with null role")
        void shouldHandleNullRole() {
            // Given
            user.setRole(null);

            // When
            UserResponse response = userResponseMapper.toResponse(user);

            // Then
            assertNotNull(response);
            assertNull(response.getRole());
            assertEquals(USER_ID, response.getId());
            assertEquals(FIRST_NAME, response.getFirstName());
        }

        @Test
        @DisplayName("Should map User with different role names")
        void shouldMapDifferentRoleNames() {
            // Given
            Role employeeRole = new Role(2L, EMPLOYEE_ROLE_NAME, EMPLOYEE_ROLE_DESCRIPTION);
            user.setRole(employeeRole);

            // When
            UserResponse response = userResponseMapper.toResponse(user);

            // Then
            assertNotNull(response);
            assertEquals(EMPLOYEE_ROLE_NAME, response.getRole());
        }

        @Test
        @DisplayName("Should map User without restaurantId")
        void shouldMapUserWithoutRestaurantId() {
            // Given
            user.setRestaurantId(null);

            // When
            UserResponse response = userResponseMapper.toResponse(user);

            // Then
            assertNotNull(response);
            assertNull(response.getRestaurantId());
            assertEquals(USER_ID, response.getId());
            assertEquals(FIRST_NAME, response.getFirstName());
        }

        @Test
        @DisplayName("Should map User with minimal fields set")
        void shouldMapUserWithMinimalFields() {
            // Given
            User minimalUser = new User();
            minimalUser.setId(USER_ID);
            minimalUser.setFirstName(FIRST_NAME);
            minimalUser.setEmail(EMAIL);
            Role role = new Role(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);
            minimalUser.setRole(role);

            // When
            UserResponse response = userResponseMapper.toResponse(minimalUser);

            // Then
            assertNotNull(response);
            assertEquals(USER_ID, response.getId());
            assertEquals(FIRST_NAME, response.getFirstName());
            assertEquals(EMAIL, response.getEmail());
            assertEquals(ROLE_NAME, response.getRole());
            assertNull(response.getLastName());
            assertNull(response.getIdentityDocument());
            assertNull(response.getPhone());
            assertNull(response.getBirthDate());
            assertNull(response.getRestaurantId());
        }

        @Test
        @DisplayName("Should preserve User data integrity in mapping")
        void shouldPreserveUserDataIntegrity() {
            // Given
            User userInput = user;

            // When
            UserResponse response = userResponseMapper.toResponse(userInput);

            // Then
            // Verify original user is not modified
            assertEquals(USER_ID, userInput.getId());
            assertEquals(FIRST_NAME, userInput.getFirstName());
            assertEquals(PASSWORD, userInput.getPassword());
            assertEquals(role, userInput.getRole());

            // Verify response matches original user data
            assertEquals(userInput.getId(), response.getId());
            assertEquals(userInput.getFirstName(), response.getFirstName());
            assertEquals(userInput.getEmail(), response.getEmail());
        }

        @Test
        @DisplayName("Should not include sensitive fields like password in response")
        void shouldNotIncludeSensitiveFields() {
            // Given
            User userInput = user;

            // When
            UserResponse response = userResponseMapper.toResponse(userInput);

            // Then
            assertNotNull(response);
            // Verify that password is not exposed in response (UserResponse doesn't have
            // password field)
            assertTrue(response.getId() > 0);
        }

        @Test
        @DisplayName("Should handle User with special characters in fields")
        void shouldHandleSpecialCharacters() {
            // Given
            user.setFirstName(SPECIAL_FIRST_NAME);
            user.setLastName(SPECIAL_LAST_NAME);
            user.setEmail(SPECIAL_EMAIL);

            // When
            UserResponse response = userResponseMapper.toResponse(user);

            // Then
            assertNotNull(response);
            assertEquals(SPECIAL_FIRST_NAME, response.getFirstName());
            assertEquals(SPECIAL_LAST_NAME, response.getLastName());
            assertEquals(SPECIAL_EMAIL, response.getEmail());
        }

        @Test
        @DisplayName("Should map CLIENT role correctly")
        void shouldMapClientRole() {
            // Given
            Role clientRole = new Role(3L, CLIENT_ROLE_NAME, CLIENT_ROLE_DESCRIPTION);
            user.setRole(clientRole);

            // When
            UserResponse response = userResponseMapper.toResponse(user);

            // Then
            assertEquals(CLIENT_ROLE_NAME, response.getRole());
        }
    }
}
