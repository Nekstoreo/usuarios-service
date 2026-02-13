package com.pragma.usuarios.infrastructure.output.jpa.adapter;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.infrastructure.output.jpa.entity.CredentialEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import com.pragma.usuarios.infrastructure.output.jpa.mapper.UserEntityMapper;
import com.pragma.usuarios.infrastructure.output.jpa.repository.ICredentialRepository;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IEmployeeRestaurantRepository;
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
    private static final Long RESTAURANT_ID = 77L;
    private static final String USER_EMAIL = "test@email.com";
    private static final String USER_PASSWORD = "encodedPassword";
    private static final String USER_DOCUMENT = "123456789";

    @Mock
    private IUserRepository userRepository;

    @Mock
    private ICredentialRepository credentialRepository;

    @Mock
    private IEmployeeRestaurantRepository employeeRestaurantRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserJpaAdapter userJpaAdapter;

    private User user;
    private UserEntity userEntity;
    private CredentialEntity credentialEntity;
    private EmployeeRestaurantEntity employeeRestaurantEntity;
    private User mappedUser;
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
        user.setPassword(USER_PASSWORD);
        user.setRestaurantId(RESTAURANT_ID);
        user.setRole(role);

        userEntity = UserEntity.builder()
                .id(USER_ID)
                .firstName("John")
                .lastName("Doe")
                .identityDocument(USER_DOCUMENT)
                .phone("+573001234567")
                .birthDate(LocalDate.of(1990, 5, 15))
                .role(roleEntity)
                .build();

        credentialEntity = CredentialEntity.builder()
            .id(3L)
            .user(userEntity)
            .email(USER_EMAIL)
            .password(USER_PASSWORD)
            .build();

        employeeRestaurantEntity = EmployeeRestaurantEntity.builder()
            .id(5L)
            .user(userEntity)
            .restaurantId(RESTAURANT_ID)
            .build();

        mappedUser = new User();
        mappedUser.setId(USER_ID);
        mappedUser.setFirstName("John");
        mappedUser.setLastName("Doe");
        mappedUser.setIdentityDocument(USER_DOCUMENT);
        mappedUser.setPhone("+573001234567");
        mappedUser.setBirthDate(LocalDate.of(1990, 5, 15));
        mappedUser.setRole(role);
    }

    @Nested
    @DisplayName("Save User Tests")
    class SaveUserTests {

        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            when(userEntityMapper.toEntity(any(User.class))).thenReturn(userEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
            when(credentialRepository.save(any(CredentialEntity.class))).thenReturn(credentialEntity);
            when(employeeRestaurantRepository.save(any(EmployeeRestaurantEntity.class))).thenReturn(employeeRestaurantEntity);
            when(userEntityMapper.toModel(any(UserEntity.class))).thenReturn(mappedUser);

            User result = userJpaAdapter.saveUser(user);

            assertNotNull(result);
            assertEquals(USER_ID, result.getId());
            assertEquals(USER_EMAIL, result.getEmail());
            assertEquals(USER_PASSWORD, result.getPassword());
            assertEquals(RESTAURANT_ID, result.getRestaurantId());
            verify(userEntityMapper).toEntity(user);
            verify(userRepository).save(userEntity);
            verify(credentialRepository).save(any(CredentialEntity.class));
            verify(employeeRestaurantRepository).save(any(EmployeeRestaurantEntity.class));
        }
    }

    @Nested
    @DisplayName("Find By Id Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find user by id when exists")
        void shouldFindUserByIdWhenExists() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userEntity));
            when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credentialEntity));
            when(employeeRestaurantRepository.findByUserId(USER_ID)).thenReturn(Optional.of(employeeRestaurantEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(mappedUser);

            Optional<User> result = userJpaAdapter.findById(USER_ID);

            assertTrue(result.isPresent());
            assertEquals(USER_ID, result.get().getId());
            assertEquals(USER_EMAIL, result.get().getEmail());
            verify(userRepository).findById(USER_ID);
        }

        @Test
        @DisplayName("Should return empty when user not found by id")
        void shouldReturnEmptyWhenUserNotFoundById() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

            Optional<User> result = userJpaAdapter.findById(USER_ID);

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
            when(credentialRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(credentialEntity));
            when(employeeRestaurantRepository.findByUserId(USER_ID)).thenReturn(Optional.of(employeeRestaurantEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(mappedUser);

            Optional<User> result = userJpaAdapter.findByEmail(USER_EMAIL);

            assertTrue(result.isPresent());
            assertEquals(USER_EMAIL, result.get().getEmail());
            verify(credentialRepository).findByEmail(USER_EMAIL);
        }

        @Test
        @DisplayName("Should return empty when user not found by email")
        void shouldReturnEmptyWhenUserNotFoundByEmail() {
            when(credentialRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

            Optional<User> result = userJpaAdapter.findByEmail(USER_EMAIL);

            assertTrue(result.isEmpty());
            verify(credentialRepository).findByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Find By Identity Document Tests")
    class FindByIdentityDocumentTests {

        @Test
        @DisplayName("Should find user by identity document when exists")
        void shouldFindUserByIdentityDocumentWhenExists() {
            when(userRepository.findByIdentityDocument(USER_DOCUMENT)).thenReturn(Optional.of(userEntity));
            when(credentialRepository.findByUserId(USER_ID)).thenReturn(Optional.of(credentialEntity));
            when(employeeRestaurantRepository.findByUserId(USER_ID)).thenReturn(Optional.of(employeeRestaurantEntity));
            when(userEntityMapper.toModel(userEntity)).thenReturn(mappedUser);

            Optional<User> result = userJpaAdapter.findByIdentityDocument(USER_DOCUMENT);

            assertTrue(result.isPresent());
            assertEquals(USER_DOCUMENT, result.get().getIdentityDocument());
            assertEquals(USER_EMAIL, result.get().getEmail());
            verify(userRepository).findByIdentityDocument(USER_DOCUMENT);
        }

        @Test
        @DisplayName("Should return empty when user not found by identity document")
        void shouldReturnEmptyWhenUserNotFoundByIdentityDocument() {
            when(userRepository.findByIdentityDocument(USER_DOCUMENT)).thenReturn(Optional.empty());

            Optional<User> result = userJpaAdapter.findByIdentityDocument(USER_DOCUMENT);

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
            when(credentialRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

            boolean result = userJpaAdapter.existsByEmail(USER_EMAIL);

            assertTrue(result);
            verify(credentialRepository).existsByEmail(USER_EMAIL);
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            when(credentialRepository.existsByEmail(USER_EMAIL)).thenReturn(false);

            boolean result = userJpaAdapter.existsByEmail(USER_EMAIL);

            assertFalse(result);
            verify(credentialRepository).existsByEmail(USER_EMAIL);
        }
    }

    @Nested
    @DisplayName("Exists By Identity Document Tests")
    class ExistsByIdentityDocumentTests {

        @Test
        @DisplayName("Should return true when identity document exists")
        void shouldReturnTrueWhenIdentityDocumentExists() {
            when(userRepository.existsByIdentityDocument(USER_DOCUMENT)).thenReturn(true);

            boolean result = userJpaAdapter.existsByIdentityDocument(USER_DOCUMENT);

            assertTrue(result);
            verify(userRepository).existsByIdentityDocument(USER_DOCUMENT);
        }

        @Test
        @DisplayName("Should return false when identity document does not exist")
        void shouldReturnFalseWhenIdentityDocumentDoesNotExist() {
            when(userRepository.existsByIdentityDocument(USER_DOCUMENT)).thenReturn(false);

            boolean result = userJpaAdapter.existsByIdentityDocument(USER_DOCUMENT);

            assertFalse(result);
            verify(userRepository).existsByIdentityDocument(USER_DOCUMENT);
        }
    }
}
