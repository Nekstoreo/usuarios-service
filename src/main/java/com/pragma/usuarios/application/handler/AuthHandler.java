package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.LoginRequest;
import com.pragma.usuarios.application.dto.response.AuthResponse;
import com.pragma.usuarios.domain.api.IAuthServicePort;
import com.pragma.usuarios.domain.spi.IJwtPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthHandler implements IAuthHandler {

    private final IAuthServicePort authServicePort;
    private final IJwtPort jwtPort;

    public AuthHandler(IAuthServicePort authServicePort, IJwtPort jwtPort) {
        this.authServicePort = authServicePort;
        this.jwtPort = jwtPort;
    }

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000L; // 15 minutes

    private static final class Attempt {
        int failures;
        long lastFailedAt;
    }

    private final java.util.concurrent.ConcurrentHashMap<String, Attempt> attempts = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail() == null ? null : request.getEmail().trim().toLowerCase();
        long now = System.currentTimeMillis();

        Attempt a = attempts.get(email);
        if (a != null && a.failures >= MAX_FAILED_ATTEMPTS) {
            if (now - a.lastFailedAt < LOCK_DURATION_MS) {
                throw new com.pragma.usuarios.domain.exception.TooManyLoginAttemptsException();
            } else {
                attempts.remove(email);
                a = null;
            }
        }

        try {
            String token = authServicePort.authenticate(email, request.getPassword());

            attempts.remove(email);

            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(jwtPort.extractUserId(token))
                    .email(jwtPort.extractEmail(token))
                    .role(jwtPort.extractRole(token))
                    .build();
        } catch (com.pragma.usuarios.domain.exception.InvalidCredentialsException ex) {
            attempts.compute(email, (k, v) -> {
                if (v == null) v = new Attempt();
                v.failures++;
                v.lastFailedAt = System.currentTimeMillis();
                return v;
            });

            Attempt updated = attempts.get(email);
            if (updated != null && updated.failures >= MAX_FAILED_ATTEMPTS) {
                throw new com.pragma.usuarios.domain.exception.TooManyLoginAttemptsException();
            }

            throw ex;
        }
    }
}
