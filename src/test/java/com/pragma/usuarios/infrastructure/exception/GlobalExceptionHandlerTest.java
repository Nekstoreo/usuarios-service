package com.pragma.usuarios.infrastructure.exception;

import com.pragma.usuarios.domain.exception.UserAlreadyExistsException;
import com.pragma.usuarios.domain.exception.UserNotFoundException;
import com.pragma.usuarios.domain.exception.UserUnderageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {
    private static final String TEST_URI = "/api/test";
    private static final String UNDERAGE_MESSAGE = "Underage";
    private static final String CONFLICT_MESSAGE = "Conflict";
    private static final String NOT_FOUND_MESSAGE = "Not found";
    private static final String NOT_FOUND_ERROR = "Not Found";
    private static final String CRITICAL_FAIL_MESSAGE = "Critical fail";
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        request.setRequestURI(TEST_URI);
    }

    @Test
    void handleValidationDomainExceptions_ShouldReturnBadRequest() {
        UserUnderageException ex = new UserUnderageException(UNDERAGE_MESSAGE);
        ResponseEntity<ErrorResponse> response = handler.handleValidationDomainExceptions(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(UNDERAGE_MESSAGE, response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Validation Error", response.getBody().getError());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException(CONFLICT_MESSAGE);
        ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExistsException(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(CONFLICT_MESSAGE, response.getBody().getMessage());
        assertEquals(HttpStatus.CONFLICT.value(), response.getBody().getStatus());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals(TEST_URI, response.getBody().getPath());
    }

    @Test
    void handleNotFoundExceptions_ShouldReturnNotFound() {
        UserNotFoundException ex = new UserNotFoundException(NOT_FOUND_MESSAGE);
        ResponseEntity<ErrorResponse> response = handler.handleNotFoundExceptions(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_FOUND_MESSAGE, response.getBody().getMessage());
        assertEquals(NOT_FOUND_ERROR, response.getBody().getError());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception ex = new Exception(CRITICAL_FAIL_MESSAGE);
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(UNEXPECTED_ERROR_MESSAGE, response.getBody().getMessage());
    }
}
