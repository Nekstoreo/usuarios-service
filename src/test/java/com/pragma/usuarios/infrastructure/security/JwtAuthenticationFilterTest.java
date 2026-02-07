package com.pragma.usuarios.infrastructure.security;

import com.pragma.usuarios.domain.spi.IJwtPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BASIC_AUTH = "Basic 123";
    private static final String VALID_TOKEN = "valid.token.here";
    private static final String TEST_EMAIL = "test@test.com";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final Long USER_ID = 1L;

    @Mock
    private IJwtPort jwtPort;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldContinue_WhenNoAuthHeader() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldContinue_WhenHeaderNotBearer() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BASIC_AUTH);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldAuthenticate_WhenTokenIsValid() throws ServletException, IOException {
        String token = VALID_TOKEN;
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + token);
        when(jwtPort.isTokenValid(token)).thenReturn(true);
        when(jwtPort.extractEmail(token)).thenReturn(TEST_EMAIL);
        when(jwtPort.extractRole(token)).thenReturn(ADMIN_ROLE);
        when(jwtPort.extractUserId(token)).thenReturn(USER_ID);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtPort).isTokenValid(token);
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(TEST_EMAIL, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
