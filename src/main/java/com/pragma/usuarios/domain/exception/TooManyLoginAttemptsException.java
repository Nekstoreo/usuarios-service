package com.pragma.usuarios.domain.exception;

public class TooManyLoginAttemptsException extends DomainException {

    public TooManyLoginAttemptsException() {
        super("Too many failed login attempts. Please try again later.");
    }
}
