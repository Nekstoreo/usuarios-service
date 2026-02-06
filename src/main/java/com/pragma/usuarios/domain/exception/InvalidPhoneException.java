package com.pragma.usuarios.domain.exception;

public class InvalidPhoneException extends ValidationException {

    public InvalidPhoneException(String message) {
        super(message);
    }
}
