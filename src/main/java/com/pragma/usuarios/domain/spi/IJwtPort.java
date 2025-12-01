package com.pragma.usuarios.domain.spi;

import com.pragma.usuarios.domain.model.User;

public interface IJwtPort {

    String generateToken(User user);

    String extractEmail(String token);

    String extractRole(String token);

    Long extractUserId(String token);

    boolean isTokenValid(String token);
}
