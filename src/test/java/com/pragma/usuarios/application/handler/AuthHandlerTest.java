package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.LoginRequest;
import com.pragma.usuarios.application.dto.response.AuthResponse;
import com.pragma.usuarios.domain.api.IAuthServicePort;
import com.pragma.usuarios.domain.spi.IJwtPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private IAuthServicePort authServicePort;

    @Mock
    private IJwtPort jwtPort;

    @InjectMocks
    private AuthHandler authHandler;

    private static final String EMAIL = "test@example.com";
    private static final String PASSWORD = "password123";
    private static final String TOKEN = "jwt.token.here";
    private static final Long USER_ID = 1L;
    private static final String ROLE = "OWNER";

    @Test
    @DisplayName("Should return auth response when login is successful")
    void shouldReturnAuthResponseWhenLoginIsSuccessful() {
        LoginRequest request = LoginRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        when(authServicePort.authenticate(EMAIL, PASSWORD)).thenReturn(TOKEN);
        when(jwtPort.extractUserId(TOKEN)).thenReturn(USER_ID);
        when(jwtPort.extractEmail(TOKEN)).thenReturn(EMAIL);
        when(jwtPort.extractRole(TOKEN)).thenReturn(ROLE);

        AuthResponse result = authHandler.login(request);

        assertThat(result.getToken()).isEqualTo(TOKEN);
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getRole()).isEqualTo(ROLE);

        verify(authServicePort).authenticate(EMAIL, PASSWORD);
        verify(jwtPort).extractUserId(TOKEN);
        verify(jwtPort).extractEmail(TOKEN);
        verify(jwtPort).extractRole(TOKEN);
    }
}
