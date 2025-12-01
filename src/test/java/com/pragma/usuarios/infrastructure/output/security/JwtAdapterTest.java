package com.pragma.usuarios.infrastructure.output.security;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAdapterTest {

    private JwtAdapter jwtAdapter;
    private User testUser;

    private static final String SECRET = "test-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtAdapter = new JwtAdapter(SECRET, EXPIRATION_MS);

        Role ownerRole = new Role(2L, "OWNER", "Restaurant owner");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setIdentityDocument("12345678");
        testUser.setPhone("+573001234567");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setRole(ownerRole);
    }

    @Nested
    @DisplayName("Generate Token Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("Should generate a valid token")
        void shouldGenerateValidToken() {
            String token = jwtAdapter.generateToken(testUser);

            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtAdapter.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("Should include user info in token")
        void shouldIncludeUserInfoInToken() {
            String token = jwtAdapter.generateToken(testUser);

            assertThat(jwtAdapter.extractEmail(token)).isEqualTo(testUser.getEmail());
            assertThat(jwtAdapter.extractUserId(token)).isEqualTo(testUser.getId());
            assertThat(jwtAdapter.extractRole(token)).isEqualTo(testUser.getRole().getName());
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should return true for valid token")
        void shouldReturnTrueForValidToken() {
            String token = jwtAdapter.generateToken(testUser);

            assertThat(jwtAdapter.isTokenValid(token)).isTrue();
        }

        @Test
        @DisplayName("Should return false for invalid token")
        void shouldReturnFalseForInvalidToken() {
            assertThat(jwtAdapter.isTokenValid("invalid.token.here")).isFalse();
        }

        @Test
        @DisplayName("Should return false for null token")
        void shouldReturnFalseForNullToken() {
            assertThat(jwtAdapter.isTokenValid(null)).isFalse();
        }

        @Test
        @DisplayName("Should return false for empty token")
        void shouldReturnFalseForEmptyToken() {
            assertThat(jwtAdapter.isTokenValid("")).isFalse();
        }

        @Test
        @DisplayName("Should return false for expired token")
        void shouldReturnFalseForExpiredToken() {
            // Create adapter with negative expiration (already expired)
            JwtAdapter expiredAdapter = new JwtAdapter(SECRET, -1000L);
            String expiredToken = expiredAdapter.generateToken(testUser);

            assertThat(jwtAdapter.isTokenValid(expiredToken)).isFalse();
        }
    }

    @Nested
    @DisplayName("Extract Claims Tests")
    class ExtractClaimsTests {

        @Test
        @DisplayName("Should extract email from token")
        void shouldExtractEmailFromToken() {
            String token = jwtAdapter.generateToken(testUser);

            String email = jwtAdapter.extractEmail(token);

            assertThat(email).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("Should extract user id from token")
        void shouldExtractUserIdFromToken() {
            String token = jwtAdapter.generateToken(testUser);

            Long userId = jwtAdapter.extractUserId(token);

            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should extract role from token")
        void shouldExtractRoleFromToken() {
            String token = jwtAdapter.generateToken(testUser);

            String role = jwtAdapter.extractRole(token);

            assertThat(role).isEqualTo("OWNER");
        }
    }
}
