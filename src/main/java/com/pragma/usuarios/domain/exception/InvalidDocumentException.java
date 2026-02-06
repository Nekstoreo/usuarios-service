package com.pragma.usuarios.domain.exception;

public class InvalidDocumentException extends ValidationException {

    public InvalidDocumentException(String message) {
        super(message);
    }
}
