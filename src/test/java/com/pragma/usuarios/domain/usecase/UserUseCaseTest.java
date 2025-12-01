package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.exception.*;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    private static final String OWNER_ROLE_NAME = "OWNER";
    private static final String EMPLOYEE_ROLE_NAME = "EMPLOYEE";
    private static final String CLIENT_ROLE_NAME = "CLIENT";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String OWNER_EMAIL = "juan.perez@email.com";
    private static final String OWNER_LAST_NAME = "Pérez";
    private static final String OWNER_IDENTITY_DOCUMENT = "123456789";
    private static final Long RESTAURANT_ID = 1L;

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        ownerRole = new Role(1L, OWNER_ROLE_NAME, "Restaurant owner");

        validUser = new User();
        validUser.setFirstName("Juan");
        validUser.setLastName(OWNER_LAST_NAME);
        validUser.setIdentityDocument(OWNER_IDENTITY_DOCUMENT);
        validUser.setPhone("+573001234567");
        validUser.setBirthDate(LocalDate.of(1990, 5, 15));
        validUser.setEmail(OWNER_EMAIL);
        validUser.setPassword("secret123");
    }

    @Nested
    @DisplayName("Create Owner - Success Cases")
    class CreateOwnerSuccessCases {

        @Test
        @DisplayName("Should create owner successfully with valid data")
        void shouldCreateOwnerSuccessfully() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(OWNER_ROLE_NAME)).thenReturn(Optional.of(ownerRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // Act
            User result = userUseCase.createOwner(validUser);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(ownerRole, result.getRole());
            assertEquals(ENCODED_PASSWORD, result.getPassword());
            verify(userPersistencePort).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should create owner with phone containing + symbol")
        void shouldCreateOwnerWithPhoneContainingPlusSymbol() {
            // Arrange
            validUser.setPhone("+573005698325");
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(OWNER_ROLE_NAME)).thenReturn(Optional.of(ownerRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenReturn(validUser);

            // Act
            User result = userUseCase.createOwner(validUser);

            // Assert
            assertNotNull(result);
            verify(userPersistencePort).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Owner - Email Validation")
    class EmailValidationTests {

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Arrange
            validUser.setEmail(null);

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            // Arrange
            validUser.setEmail("invalid-email");

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email is missing domain")
        void shouldThrowExceptionWhenEmailIsMissingDomain() {
            // Arrange
            validUser.setEmail("user@");

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createOwner(validUser));
        }
    }

    @Nested
    @DisplayName("Create Owner - Phone Validation")
    class PhoneValidationTests {

        @Test
        @DisplayName("Should throw exception when phone is null")
        void shouldThrowExceptionWhenPhoneIsNull() {
            // Arrange
            validUser.setPhone(null);

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone exceeds 13 characters")
        void shouldThrowExceptionWhenPhoneExceeds13Characters() {
            // Arrange
            validUser.setPhone("+5730012345678901");

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone contains invalid characters")
        void shouldThrowExceptionWhenPhoneContainsInvalidCharacters() {
            // Arrange
            validUser.setPhone("300-123-4567");

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createOwner(validUser));
        }
    }

    @Nested
    @DisplayName("Create Owner - Document Validation")
    class DocumentValidationTests {

        @Test
        @DisplayName("Should throw exception when document is null")
        void shouldThrowExceptionWhenDocumentIsNull() {
            // Arrange
            validUser.setIdentityDocument(null);

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document contains non-numeric characters")
        void shouldThrowExceptionWhenDocumentContainsNonNumericCharacters() {
            // Arrange
            validUser.setIdentityDocument("ABC123XYZ");

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document contains special characters")
        void shouldThrowExceptionWhenDocumentContainsSpecialCharacters() {
            // Arrange
            validUser.setIdentityDocument("123-456-789");

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createOwner(validUser));
        }
    }

    @Nested
    @DisplayName("Create Owner - Age Validation")
    class AgeValidationTests {

        @Test
        @DisplayName("Should throw exception when birth date is null")
        void shouldThrowExceptionWhenBirthDateIsNull() {
            // Arrange
            validUser.setBirthDate(null);

            // Act & Assert
            assertThrows(UserUnderageException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when user is underage")
        void shouldThrowExceptionWhenUserIsUnderage() {
            // Arrange
            validUser.setBirthDate(LocalDate.now().minusYears(17));

            // Act & Assert
            assertThrows(UserUnderageException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should accept user who is exactly 18 years old")
        void shouldAcceptUserWhoIsExactly18YearsOld() {
            // Arrange
            validUser.setBirthDate(LocalDate.now().minusYears(18));
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(OWNER_ROLE_NAME)).thenReturn(Optional.of(ownerRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenReturn(validUser);

            // Act
            User result = userUseCase.createOwner(validUser);

            // Assert
            assertNotNull(result);
            verify(userPersistencePort).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Owner - Duplicate Validation")
    class DuplicateValidationTests {

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(validUser.getEmail())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document already exists")
        void shouldThrowExceptionWhenDocumentAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(validUser.getIdentityDocument())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Owner - Role Validation")
    class RoleValidationTests {

        @Test
        @DisplayName("Should throw exception when owner role does not exist")
        void shouldThrowExceptionWhenOwnerRoleDoesNotExist() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(OWNER_ROLE_NAME)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RoleNotFoundException.class, () -> userUseCase.createOwner(validUser));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Owner - Password Encoding")
    class PasswordEncodingTests {

        @Test
        @DisplayName("Should encode password before saving")
        void shouldEncodePasswordBeforeSaving() {
            // Arrange
            String rawPassword = "secret123";
            String encodedPassword = "$2a$10$encodedPasswordHash";
            validUser.setPassword(rawPassword);

            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(OWNER_ROLE_NAME)).thenReturn(Optional.of(ownerRole));
            when(passwordEncoderPort.encode(rawPassword)).thenReturn(encodedPassword);
            when(userPersistencePort.saveUser(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                assertEquals(encodedPassword, user.getPassword());
                return user;
            });

            // Act
            userUseCase.createOwner(validUser);

            // Assert
            verify(passwordEncoderPort).encode(rawPassword);
        }
    }

    @Nested
    @DisplayName("Get User By ID")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return user when found by ID")
        void shouldReturnUserWhenFoundById() {
            // Arrange
            Long userId = 1L;
            validUser.setId(userId);
            validUser.setRole(ownerRole);
            when(userPersistencePort.findById(userId)).thenReturn(Optional.of(validUser));

            // Act
            Optional<User> result = userUseCase.getUserById(userId);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(userId, result.get().getId());
            assertEquals(validUser.getEmail(), result.get().getEmail());
            verify(userPersistencePort).findById(userId);
        }

        @Test
        @DisplayName("Should return empty when user not found by ID")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Arrange
            Long userId = 999L;
            when(userPersistencePort.findById(userId)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userUseCase.getUserById(userId);

            // Assert
            assertTrue(result.isEmpty());
            verify(userPersistencePort).findById(userId);
        }
    }

    @Nested
    @DisplayName("Create Employee - Success Cases")
    class CreateEmployeeSuccessCases {

        private User validEmployee;
        private Role employeeRole;

        @BeforeEach
        void setUp() {
            employeeRole = new Role(3L, EMPLOYEE_ROLE_NAME, "Restaurant employee");

            validEmployee = new User();
            validEmployee.setFirstName("Carlos");
            validEmployee.setLastName("García");
            validEmployee.setIdentityDocument("987654321");
            validEmployee.setPhone("+573009876543");
            validEmployee.setEmail("carlos.garcia@email.com");
            validEmployee.setPassword("employee123");
            validEmployee.setRestaurantId(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should create employee successfully with valid data")
        void shouldCreateEmployeeSuccessfully() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(EMPLOYEE_ROLE_NAME)).thenReturn(Optional.of(employeeRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // Act
            User result = userUseCase.createEmployee(validEmployee);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(employeeRole, result.getRole());
            assertEquals(ENCODED_PASSWORD, result.getPassword());
            assertEquals(RESTAURANT_ID, result.getRestaurantId());
            verify(userPersistencePort).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should create employee without birth date")
        void shouldCreateEmployeeWithoutBirthDate() {
            // Arrange
            validEmployee.setBirthDate(null);
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(EMPLOYEE_ROLE_NAME)).thenReturn(Optional.of(employeeRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenReturn(validEmployee);

            // Act
            User result = userUseCase.createEmployee(validEmployee);

            // Assert
            assertNotNull(result);
            assertNull(result.getBirthDate());
            verify(userPersistencePort).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Employee - Validation Errors")
    class CreateEmployeeValidationErrors {

        private User invalidEmployee;

        @BeforeEach
        void setUp() {

            invalidEmployee = new User();
            invalidEmployee.setFirstName("Carlos");
            invalidEmployee.setLastName("García");
            invalidEmployee.setIdentityDocument("987654321");
            invalidEmployee.setPhone("+573009876543");
            invalidEmployee.setEmail("carlos.garcia@email.com");
            invalidEmployee.setPassword("employee123");
            invalidEmployee.setRestaurantId(RESTAURANT_ID);
        }

        @Test
        @DisplayName("Should throw exception when email is invalid")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // Arrange
            invalidEmployee.setEmail("invalid-email");

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone is invalid")
        void shouldThrowExceptionWhenPhoneIsInvalid() {
            // Arrange
            invalidEmployee.setPhone("invalid-phone");

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document is invalid")
        void shouldThrowExceptionWhenDocumentIsInvalid() {
            // Arrange
            invalidEmployee.setIdentityDocument("ABC123");

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when restaurant ID is null")
        void shouldThrowExceptionWhenRestaurantIdIsNull() {
            // Arrange
            invalidEmployee.setRestaurantId(null);

            // Act & Assert
            assertThrows(InvalidRestaurantException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(invalidEmployee.getEmail())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document already exists")
        void shouldThrowExceptionWhenDocumentAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(invalidEmployee.getIdentityDocument())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when employee role does not exist")
        void shouldThrowExceptionWhenEmployeeRoleDoesNotExist() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(EMPLOYEE_ROLE_NAME)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RoleNotFoundException.class, () -> userUseCase.createEmployee(invalidEmployee));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Client - Success Cases")
    class CreateClientSuccessCases {

        private User validClient;
        private Role clientRole;

        @BeforeEach
        void setUp() {
            clientRole = new Role(4L, CLIENT_ROLE_NAME, "Platform client");

            validClient = new User();
            validClient.setFirstName("María");
            validClient.setLastName("Rodríguez");
            validClient.setIdentityDocument("456789123");
            validClient.setPhone("+573005554321");
            validClient.setEmail("maria.rodriguez@email.com");
            validClient.setPassword("client123");
        }

        @Test
        @DisplayName("Should create client successfully with valid data")
        void shouldCreateClientSuccessfully() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(CLIENT_ROLE_NAME)).thenReturn(Optional.of(clientRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // Act
            User result = userUseCase.createClient(validClient);

            // Assert
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(clientRole, result.getRole());
            assertEquals(ENCODED_PASSWORD, result.getPassword());
            assertNull(result.getBirthDate());
            verify(userPersistencePort).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should create client without birth date")
        void shouldCreateClientWithoutBirthDate() {
            // Arrange
            validClient.setBirthDate(null);
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(CLIENT_ROLE_NAME)).thenReturn(Optional.of(clientRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenReturn(validClient);

            // Act
            User result = userUseCase.createClient(validClient);

            // Assert
            assertNotNull(result);
            assertNull(result.getBirthDate());
            verify(userPersistencePort).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should create client with phone containing + symbol")
        void shouldCreateClientWithPhoneContainingPlusSymbol() {
            // Arrange
            validClient.setPhone("+573005698325");
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(CLIENT_ROLE_NAME)).thenReturn(Optional.of(clientRole));
            when(passwordEncoderPort.encode(anyString())).thenReturn(ENCODED_PASSWORD);
            when(userPersistencePort.saveUser(any(User.class))).thenReturn(validClient);

            // Act
            User result = userUseCase.createClient(validClient);

            // Assert
            assertNotNull(result);
            verify(userPersistencePort).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("Create Client - Validation Errors")
    class CreateClientValidationErrors {

        private User invalidClient;

        @BeforeEach
        void setUp() {
            invalidClient = new User();
            invalidClient.setFirstName("María");
            invalidClient.setLastName("Rodríguez");
            invalidClient.setIdentityDocument("456789123");
            invalidClient.setPhone("+573005554321");
            invalidClient.setEmail("maria.rodriguez@email.com");
            invalidClient.setPassword("client123");
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Arrange
            invalidClient.setEmail(null);

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email format is invalid")
        void shouldThrowExceptionWhenEmailFormatIsInvalid() {
            // Arrange
            invalidClient.setEmail("invalid-email");

            // Act & Assert
            assertThrows(InvalidEmailException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone is null")
        void shouldThrowExceptionWhenPhoneIsNull() {
            // Arrange
            invalidClient.setPhone(null);

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone exceeds 13 characters")
        void shouldThrowExceptionWhenPhoneExceeds13Characters() {
            // Arrange
            invalidClient.setPhone("+5730012345678901");

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when phone contains invalid characters")
        void shouldThrowExceptionWhenPhoneContainsInvalidCharacters() {
            // Arrange
            invalidClient.setPhone("300-123-4567");

            // Act & Assert
            assertThrows(InvalidPhoneException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document is null")
        void shouldThrowExceptionWhenDocumentIsNull() {
            // Arrange
            invalidClient.setIdentityDocument(null);

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document contains non-numeric characters")
        void shouldThrowExceptionWhenDocumentContainsNonNumericCharacters() {
            // Arrange
            invalidClient.setIdentityDocument("ABC123XYZ");

            // Act & Assert
            assertThrows(InvalidDocumentException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(invalidClient.getEmail())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when document already exists")
        void shouldThrowExceptionWhenDocumentAlreadyExists() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(invalidClient.getIdentityDocument())).thenReturn(true);

            // Act & Assert
            assertThrows(UserAlreadyExistsException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when client role does not exist")
        void shouldThrowExceptionWhenClientRoleDoesNotExist() {
            // Arrange
            when(userPersistencePort.existsByEmail(anyString())).thenReturn(false);
            when(userPersistencePort.existsByIdentityDocument(anyString())).thenReturn(false);
            when(rolePersistencePort.findByName(CLIENT_ROLE_NAME)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RoleNotFoundException.class, () -> userUseCase.createClient(invalidClient));
            verify(userPersistencePort, never()).saveUser(any(User.class));
        }
    }
}


