package com.pragma.usuarios.domain.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("User is not authorized to perform this action");
    }
}
