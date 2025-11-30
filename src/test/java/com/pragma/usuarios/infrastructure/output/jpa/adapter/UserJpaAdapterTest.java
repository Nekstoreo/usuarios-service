package com.pragma.usuarios.infrastructure.output.jpa.adapter;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import com.pragma.usuarios.infrastructure.output.jpa.mapper.UserEntityMapper;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IUserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserJpaAdapterTest {

    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "test@email.com";
    private static final String USER_DOCUMENT = "123456789";

    @Mock
    private IUserRepository userRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;

    private User user;
    private UserEntity userEntity;
    private Role role;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "OWNER", "Restaurant owner");
        roleEntity = new RoleEntity(1L, "OWNER", "Restaurant owner");

        user = new User();
        user.setId(USER_ID);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIdentityDocument(USER_DOCUMENT);
        user.setPhone("+573001234567");
        user.setBirthDate(LocalDate.of(1990, 5, 15));
        user.setEmail(USER_EMAIL);
        user.setPassword("encodedPassword");
        user.setRole(role);

        userEntity = UserEntity.builder()
                .id(USER_ID)
                .firstName("John")
                .lastName("Doe")
                .identityDocument(USER_DOCUMENT)
                .phone("+573001234567")
                .birthDate(LocalDate.of(1990, 5, 15))
                .email(USER_EMAIL)
                .password("encodedPassword")
                .role(roleEntity)
                .build();
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            // Arrange
            when(userEntityMapper.toEntity(any(User.class))).thenReturn(userEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
            when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(user);

            // Act
            User result = userJpaAdapter.saveUser(user);

            // Assert
            assertNotNull(result);
            assertEquals(USER_ID, result.getId());
            assertEquals(USER_EMAIL, result.getEmail());
            verify(userEntityMapper).toEntity(user);
            verify(userRepository).save(userEntity);
            verify(userEntityMapper).toModel(userEntity);
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find user by id when exists")
        void shouldFindUserByIdWhenExists() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(user);

            // Act
            Optional<User> result = userJpaAdapter.findById(USER_ID);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(USER_ID, result.get().getId());
            verify(userRepository).findById(USER_ID);
        }

        @Test
        @DisplayName("Should return empty when user not found by id")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Arrange
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userJpaAdapter.findById(USER_ID);

            // Assert
            assertTrue(result.isEmpty());
            verify(userRepository).findById(USER_ID);
            verify(userEntityMapper, never()).toModel(any());
        }
    }

    @Nested
    @DisplayName("Find By Email Tests")
    class FindByEmailTests {

        @Test
        @DisplayName("Should find user by email when exists")
        void shouldFindUserByEmailWhenExists() {
            // Arrange
            when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(userEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(user);

            // Act
            Optional<User> result = userJpaAdapter.findByEmail(USER_EMAIL);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(USER_EMAIL, result.get().getEmail());
            verify(userRepository).findByEmail(USER_EMAIL);
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            // Arrange
            when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userJpaAdapter.findByEmail(USER_EMAIL);

            // Assert
            assertTrue(result.isEmpty());
            verify(userRepository).findByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Find By Identity Document Tests")
    class FindByIdentityDocumentTests {

        @Test
        @DisplayName("Should find user by identity document when exists")
        void shouldFindUserByIdentityDocumentWhenExists() {
            // Arrange
            when(userRepository.findByIdentityDocument(USER_DOCUMENT)).thenReturn(Optional.of(userEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(user);

            // Act
            Optional<User> result = userJpaAdapter.findByIdentityDocument(USER_DOCUMENT);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(USER_DOCUMENT, result.get().getIdentityDocument());
            verify(userRepository).findByIdentityDocument(USER_DOCUMENT);
        }

        @Test
        @DisplayName("Should return empty when user not found by identity document")
        void shouldReturnEmptyWhenUserNotFoundByIdentityDocument() {
            // Arrange
            when(userRepository.findByIdentityDocument(USER_DOCUMENT)).thenReturn(Optional.empty());

            // Act
            Optional<User> result = userJpaAdapter.findByIdentityDocument(USER_DOCUMENT);

            // Assert
            assertTrue(result.isEmpty());
            verify(userRepository).findByIdentityDocument(USER_DOCUMENT);
        }
    }

    @Nested
    @DisplayName("Exists By Email Tests")
    class ExistsByEmailTests {

        @Test
        @DisplayName("Should return true when email exists")
        void shouldReturnTrueWhenEmailExists() {
            // Arrange
            when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

            // Act
            boolean result = userJpaAdapter.existsByEmail(USER_EMAIL);

            // Assert
            assertTrue(result);
            verify(userRepository).existsByEmail(USER_EMAIL);
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            // Arrange
            when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);

            // Act
            boolean result = userJpaAdapter.existsByEmail(USER_EMAIL);

            // Assert
            assertFalse(result);
            verify(userRepository).existsByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Exists By Identity Document Tests")
    class ExistsByIdentityDocumentTests {

        @Test
        @DisplayName("Should return true when identity document exists")
        void shouldReturnTrueWhenIdentityDocumentExists() {
            // Arrange
            when(userRepository.existsByIdentityDocument(USER_DOCUMENT)).thenReturn(true);

            // Act
            boolean result = userJpaAdapter.existsByIdentityDocument(USER_DOCUMENT);

            // Assert
            assertTrue(result);
            verify(userRepository).existsByIdentityDocument(USER_DOCUMENT);
        }

        @Test
        @DisplayName("Should return false when identity document does not exist")
        void shouldReturnFalseWhenIdentityDocumentDoesNotExist() {
            // Arrange
            when(userRepository.existsByIdentityDocument(USER_DOCUMENT)).thenReturn(false);

            // Act
            boolean result = userJpaAdapter.existsByIdentityDocument(USER_DOCUMENT);

            // Assert
            assertFalse(result);
            verify(userRepository).existsByIdentityDocument(USER_DOCUMENT);
        }
    }
}
