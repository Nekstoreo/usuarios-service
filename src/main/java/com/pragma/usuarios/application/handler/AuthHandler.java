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

    @Override
    public AuthResponse login(LoginRequest request) {
        String token = authServicePort.authenticate(request.getEmail(), request.getPassword());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(jwtPort.extractUserId(token))
                .email(jwtPort.extractEmail(token))
                .role(jwtPort.extractRole(token))
                .build();
    }
}
