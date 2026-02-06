package com.pragma.usuarios.domain.exception;

public class UserNotFoundException extends ResourceException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
