package com.pragma.usuarios.application.mapper;

import com.pragma.usuarios.application.dto.request.CreateClientRequest;
import com.pragma.usuarios.application.dto.request.CreateEmployeeRequest;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRequestMapper Tests")
class UserRequestMapperTest {

    private static final String FIRST_NAME = "Juan";
    private static final String LAST_NAME = "Pérez";
    private static final String IDENTITY_DOCUMENT = "123456789";
    private static final String PHONE = "+573001234567";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 5, 15);
    private static final String EMAIL = "juan.perez@example.com";
    private static final String PASSWORD = "password123";
    private static final Long RESTAURANT_ID = 1L;

    private UserRequestMapper userRequestMapper;

    @BeforeEach
    void setUp() {
        userRequestMapper = Mappers.getMapper(UserRequestMapper.class);
    }

    @Nested
    @DisplayName("toUser(CreateOwnerRequest) Tests")
    class ToUserCreateOwnerRequestTests {

        private CreateOwnerRequest createOwnerRequest;

        @BeforeEach
        void setUp() {
            createOwnerRequest = new CreateOwnerRequest();
            createOwnerRequest.setFirstName(FIRST_NAME);
            createOwnerRequest.setLastName(LAST_NAME);
            createOwnerRequest.setIdentityDocument(IDENTITY_DOCUMENT);
            createOwnerRequest.setPhone(PHONE);
            createOwnerRequest.setBirthDate(BIRTH_DATE);
            createOwnerRequest.setEmail(EMAIL);
            createOwnerRequest.setPassword(PASSWORD);
        }

        @Test
        @DisplayName("Should map CreateOwnerRequest to User with all fields correctly mapped")
        void shouldMapCreateOwnerRequestToUser() {
            // Given
            CreateOwnerRequest request = createOwnerRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(FIRST_NAME, user.getFirstName());
            assertEquals(LAST_NAME, user.getLastName());
            assertEquals(IDENTITY_DOCUMENT, user.getIdentityDocument());
            assertEquals(PHONE, user.getPhone());
            assertEquals(BIRTH_DATE, user.getBirthDate());
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PASSWORD, user.getPassword());
            assertNull(user.getId());
            assertNull(user.getRole());
            assertNull(user.getRestaurantId());
        }

        @Test
        @DisplayName("Should map CreateOwnerRequest and ignore id, role, restaurantId fields")
        void shouldIgnoreIdRoleAndRestaurantIdFields() {
            // Given
            CreateOwnerRequest request = createOwnerRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNull(user.getId());
            assertNull(user.getRole());
            assertNull(user.getRestaurantId());
        }

        @Test
        @DisplayName("Should handle null CreateOwnerRequest values")
        void shouldHandleNullValues() {
            // Given
            CreateOwnerRequest request = new CreateOwnerRequest();
            request.setFirstName(FIRST_NAME);
            request.setLastName(LAST_NAME);

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(FIRST_NAME, user.getFirstName());
            assertEquals(LAST_NAME, user.getLastName());
            assertNull(user.getIdentityDocument());
            assertNull(user.getPhone());
            assertNull(user.getBirthDate());
            assertNull(user.getEmail());
            assertNull(user.getPassword());
        }
    }

    @Nested
    @DisplayName("toUser(CreateEmployeeRequest) Tests")
    class ToUserCreateEmployeeRequestTests {

        private CreateEmployeeRequest createEmployeeRequest;

        @BeforeEach
        void setUp() {
            createEmployeeRequest = new CreateEmployeeRequest();
            createEmployeeRequest.setFirstName(FIRST_NAME);
            createEmployeeRequest.setLastName(LAST_NAME);
            createEmployeeRequest.setIdentityDocument(IDENTITY_DOCUMENT);
            createEmployeeRequest.setPhone(PHONE);
            createEmployeeRequest.setEmail(EMAIL);
            createEmployeeRequest.setPassword(PASSWORD);
            createEmployeeRequest.setRestaurantId(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should map CreateEmployeeRequest to User with all fields correctly mapped")
        void shouldMapCreateEmployeeRequestToUser() {
            // Given
            CreateEmployeeRequest request = createEmployeeRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(FIRST_NAME, user.getFirstName());
            assertEquals(LAST_NAME, user.getLastName());
            assertEquals(IDENTITY_DOCUMENT, user.getIdentityDocument());
            assertEquals(PHONE, user.getPhone());
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PASSWORD, user.getPassword());
            assertNull(user.getId());
            assertNull(user.getRole());
        }

        @Test
        @DisplayName("Should map CreateEmployeeRequest and ignore id and role fields")
        void shouldIgnoreIdAndRoleFields() {
            // Given
            CreateEmployeeRequest request = createEmployeeRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNull(user.getId());
            assertNull(user.getRole());
        }

        @Test
        @DisplayName("Should map restaurantId field from CreateEmployeeRequest")
        void shouldMapRestaurantIdField() {
            // Given
            CreateEmployeeRequest request = createEmployeeRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertEquals(RESTAURANT_ID, user.getRestaurantId());
        }

        @Test
        @DisplayName("Should handle minimal CreateEmployeeRequest")
        void shouldHandleMinimalEmployeeRequest() {
            // Given
            CreateEmployeeRequest request = new CreateEmployeeRequest();
            request.setFirstName(FIRST_NAME);
            request.setLastName(LAST_NAME);

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(FIRST_NAME, user.getFirstName());
            assertEquals(LAST_NAME, user.getLastName());
            assertNull(user.getIdentityDocument());
            assertNull(user.getEmail());
        }
    }

    @Nested
    @DisplayName("toUser(CreateClientRequest) Tests")
    class ToUserCreateClientRequestTests {

        private CreateClientRequest createClientRequest;

        @BeforeEach
        void setUp() {
            createClientRequest = new CreateClientRequest();
            createClientRequest.setFirstName(FIRST_NAME);
            createClientRequest.setLastName(LAST_NAME);
            createClientRequest.setIdentityDocument(IDENTITY_DOCUMENT);
            createClientRequest.setPhone(PHONE);
            createClientRequest.setEmail(EMAIL);
            createClientRequest.setPassword(PASSWORD);
        }

        @Test
        @DisplayName("Should map CreateClientRequest to User with all fields correctly mapped")
        void shouldMapCreateClientRequestToUser() {
            // Given
            CreateClientRequest request = createClientRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(FIRST_NAME, user.getFirstName());
            assertEquals(LAST_NAME, user.getLastName());
            assertEquals(IDENTITY_DOCUMENT, user.getIdentityDocument());
            assertEquals(PHONE, user.getPhone());
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PASSWORD, user.getPassword());
            assertNull(user.getId());
            assertNull(user.getRole());
            assertNull(user.getRestaurantId());
        }

        @Test
        @DisplayName("Should map CreateClientRequest and ignore id, role, restaurantId fields")
        void shouldIgnoreIdRoleAndRestaurantIdFields() {
            // Given
            CreateClientRequest request = createClientRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNull(user.getId());
            assertNull(user.getRole());
            assertNull(user.getRestaurantId());
        }

        @Test
        @DisplayName("Should not include birthDate in CreateClientRequest mapping")
        void shouldNotIncludeBirthDate() {
            // Given
            CreateClientRequest request = createClientRequest;

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNull(user.getBirthDate());
        }

        @Test
        @DisplayName("Should handle partial CreateClientRequest")
        void shouldHandlePartialClientRequest() {
            // Given
            CreateClientRequest request = new CreateClientRequest();
            request.setEmail(EMAIL);
            request.setPassword(PASSWORD);

            // When
            User user = userRequestMapper.toUser(request);

            // Then
            assertNotNull(user);
            assertEquals(EMAIL, user.getEmail());
            assertEquals(PASSWORD, user.getPassword());
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
        }
    }
}
