package com.pragma.usuarios.infrastructure.exception;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    private static final String ERROR_MESSAGE = "Error occurred";
    private static final int NOT_FOUND_STATUS = 404;
    private static final String NOT_FOUND_ERROR = "Not Found";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String USER_PATH = "/api/users/1";
    private static final int INTERNAL_ERROR_STATUS = 500;
    private static final String INTERNAL_ERROR = "Internal Error";
    private static final String SOMETHING_FAILED_MESSAGE = "Something failed";
    private static final String TEST_PATH = "/test";

    @Test
    void noArgConstructor_ShouldSetTimestamp() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response.getTimestamp());
    }

    @Test
    void messageConstructor_ShouldSetMessageAndTimestamp() {
        String msg = ERROR_MESSAGE;
        ErrorResponse response = new ErrorResponse(msg);
        assertNotNull(response.getTimestamp());
        assertEquals(msg, response.getMessage());
    }

    @Test
    void fullConstructor_ShouldSetAllFields() {
        LocalDateTime before = LocalDateTime.now();
        ErrorResponse response = new ErrorResponse(NOT_FOUND_STATUS, NOT_FOUND_ERROR, USER_NOT_FOUND_MESSAGE,
                USER_PATH);

        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(before) || response.getTimestamp().isEqual(before));
        assertEquals(NOT_FOUND_STATUS, response.getStatus());
        assertEquals(NOT_FOUND_ERROR, response.getError());
        assertEquals(USER_NOT_FOUND_MESSAGE, response.getMessage());
        assertEquals(USER_PATH, response.getPath());
    }

    @Test
    void setters_ShouldUpdateFields() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();
        response.setTimestamp(now);
        response.setStatus(INTERNAL_ERROR_STATUS);
        response.setError(INTERNAL_ERROR);
        response.setMessage(SOMETHING_FAILED_MESSAGE);
        response.setPath(TEST_PATH);

        assertEquals(now, response.getTimestamp());
        assertEquals(INTERNAL_ERROR_STATUS, response.getStatus());
        assertEquals(INTERNAL_ERROR, response.getError());
        assertEquals(SOMETHING_FAILED_MESSAGE, response.getMessage());
        assertEquals(TEST_PATH, response.getPath());
    }
}
