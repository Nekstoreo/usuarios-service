package com.pragma.usuarios.domain.exception;

public class InvalidRestaurantException extends ValidationException {

    public InvalidRestaurantException(String message) {
        super(message);
    }
}
