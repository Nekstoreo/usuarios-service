package com.pragma.usuarios.domain.exception;

public class UserUnderageException extends ValidationException {

    public UserUnderageException(String message) {
        super(message);
    }
}
