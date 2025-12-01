package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IAuthServicePort;
import com.pragma.usuarios.domain.exception.InvalidCredentialsException;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IJwtPort;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;

public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final IJwtPort jwtPort;

    public AuthUseCase(IUserPersistencePort userPersistencePort,
                       IPasswordEncoderPort passwordEncoderPort,
                       IJwtPort jwtPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.jwtPort = jwtPort;
    }

    @Override
    public String authenticate(String email, String password) {
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return jwtPort.generateToken(user);
    }

    @Override
    public User validateToken(String token) {
        if (!jwtPort.isTokenValid(token)) {
            throw new InvalidCredentialsException();
        }

        String email = jwtPort.extractEmail(token);
        return userPersistencePort.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
