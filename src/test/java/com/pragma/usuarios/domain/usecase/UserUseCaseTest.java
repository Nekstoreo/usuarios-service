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
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String OWNER_EMAIL = "juan.perez@email.com";
    private static final String OWNER_LAST_NAME = "Pérez";
    private static final String OWNER_IDENTITY_DOCUMENT = "123456789";

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
}
