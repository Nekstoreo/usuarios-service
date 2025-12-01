package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.exception.InvalidCredentialsException;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IJwtPort;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private IJwtPort jwtPort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User testUser;
    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPassword";
    private static final String TOKEN = "jwt.token.here";

    @BeforeEach
    void setUp() {
        Role ownerRole = new Role(2L, "OWNER", "Restaurant owner");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail(EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setIdentityDocument("12345678");
        testUser.setPhone("+573001234567");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setRole(ownerRole);
    }

    @Nested
    @DisplayName("Authenticate Tests")
    class AuthenticateTests {

        @Test
        @DisplayName("Should return token when credentials are valid")
        void shouldReturnTokenWhenCredentialsAreValid() {
            when(userPersistencePort.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
            when(passwordEncoderPort.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
            when(jwtPort.generateToken(testUser)).thenReturn(TOKEN);

            String result = authUseCase.authenticate(EMAIL, PASSWORD);

            assertThat(result).isEqualTo(TOKEN);
            verify(userPersistencePort).findByEmail(EMAIL);
            verify(passwordEncoderPort).matches(PASSWORD, ENCODED_PASSWORD);
            verify(jwtPort).generateToken(testUser);
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userPersistencePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authUseCase.authenticate(EMAIL, PASSWORD))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email or password");

            verify(userPersistencePort).findByEmail(EMAIL);
            verifyNoInteractions(passwordEncoderPort);
            verifyNoInteractions(jwtPort);
        }

        @Test
        @DisplayName("Should throw exception when password is incorrect")
        void shouldThrowExceptionWhenPasswordIsIncorrect() {
            when(userPersistencePort.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
            when(passwordEncoderPort.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

            assertThatThrownBy(() -> authUseCase.authenticate(EMAIL, PASSWORD))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email or password");

            verify(userPersistencePort).findByEmail(EMAIL);
            verify(passwordEncoderPort).matches(PASSWORD, ENCODED_PASSWORD);
            verifyNoInteractions(jwtPort);
        }
    }

    @Nested
    @DisplayName("Validate Token Tests")
    class ValidateTokenTests {

        @Test
        @DisplayName("Should return user when token is valid")
        void shouldReturnUserWhenTokenIsValid() {
            when(jwtPort.isTokenValid(TOKEN)).thenReturn(true);
            when(jwtPort.extractEmail(TOKEN)).thenReturn(EMAIL);
            when(userPersistencePort.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));

            User result = authUseCase.validateToken(TOKEN);

            assertThat(result).isEqualTo(testUser);
            verify(jwtPort).isTokenValid(TOKEN);
            verify(jwtPort).extractEmail(TOKEN);
            verify(userPersistencePort).findByEmail(EMAIL);
        }

        @Test
        @DisplayName("Should throw exception when token is invalid")
        void shouldThrowExceptionWhenTokenIsInvalid() {
            when(jwtPort.isTokenValid(TOKEN)).thenReturn(false);

            assertThatThrownBy(() -> authUseCase.validateToken(TOKEN))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(jwtPort).isTokenValid(TOKEN);
            verify(jwtPort, never()).extractEmail(any());
        }

        @Test
        @DisplayName("Should throw exception when user from token not found")
        void shouldThrowExceptionWhenUserFromTokenNotFound() {
            when(jwtPort.isTokenValid(TOKEN)).thenReturn(true);
            when(jwtPort.extractEmail(TOKEN)).thenReturn(EMAIL);
            when(userPersistencePort.findByEmail(EMAIL)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authUseCase.validateToken(TOKEN))
                    .isInstanceOf(InvalidCredentialsException.class);

            verify(jwtPort).isTokenValid(TOKEN);
            verify(jwtPort).extractEmail(TOKEN);
            verify(userPersistencePort).findByEmail(EMAIL);
        }
    }
}
