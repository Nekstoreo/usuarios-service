package com.pragma.usuarios.infrastructure.output.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderAdapterTest {

    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$encodedPasswordHash";

    @Mock
    private PasswordEncoder passwordEncoder;

    private PasswordEncoderAdapter passwordEncoderAdapter;

    @BeforeEach
    void setUp() {
        passwordEncoderAdapter = new PasswordEncoderAdapter(passwordEncoder);
    }

    @Nested
    @DisplayName("Encode Password Tests")
    class EncodePasswordTests {

        @Test
        @DisplayName("Should encode password successfully")
        void shouldEncodePasswordSuccessfully() {
            // Arrange
            when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);

            // Act
            String result = passwordEncoderAdapter.encode(RAW_PASSWORD);

            // Assert
            assertNotNull(result);
            assertEquals(ENCODED_PASSWORD, result);
            verify(passwordEncoder).encode(RAW_PASSWORD);
        }

        @Test
        @DisplayName("Should delegate encoding to Spring PasswordEncoder")
        void shouldDelegateEncodingToSpringPasswordEncoder() {
            // Arrange
            String anotherPassword = "anotherPassword";
            String anotherEncodedPassword = "$2a$10$anotherEncodedHash";
            when(passwordEncoder.encode(anotherPassword)).thenReturn(anotherEncodedPassword);

            // Act
            String result = passwordEncoderAdapter.encode(anotherPassword);

            // Assert
            assertEquals(anotherEncodedPassword, result);
            verify(passwordEncoder).encode(anotherPassword);
        }
    }

    @Nested
    @DisplayName("Matches Password Tests")
    class MatchesPasswordTests {

        @Test
        @DisplayName("Should return true when passwords match")
        void shouldReturnTrueWhenPasswordsMatch() {
            // Arrange
            when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

            // Act
            boolean result = passwordEncoderAdapter.matches(RAW_PASSWORD, ENCODED_PASSWORD);

            // Assert
            assertTrue(result);
            verify(passwordEncoder).matches(RAW_PASSWORD, ENCODED_PASSWORD);
        }

        @Test
        @DisplayName("Should return false when passwords do not match")
        void shouldReturnFalseWhenPasswordsDoNotMatch() {
            // Arrange
            String wrongPassword = "wrongPassword";
            when(passwordEncoder.matches(wrongPassword, ENCODED_PASSWORD)).thenReturn(false);

            // Act
            boolean result = passwordEncoderAdapter.matches(wrongPassword, ENCODED_PASSWORD);

            // Assert
            assertFalse(result);
            verify(passwordEncoder).matches(wrongPassword, ENCODED_PASSWORD);
        }

        @Test
        @DisplayName("Should delegate matching to Spring PasswordEncoder")
        void shouldDelegateMatchingToSpringPasswordEncoder() {
            // Arrange
            when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

            // Act
            passwordEncoderAdapter.matches(RAW_PASSWORD, ENCODED_PASSWORD);

            // Assert
            verify(passwordEncoder, times(1)).matches(RAW_PASSWORD, ENCODED_PASSWORD);
        }
    }
}
