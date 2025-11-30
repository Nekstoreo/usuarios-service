package com.pragma.usuarios.domain.exception;

public class InvalidEmailException extends DomainException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
