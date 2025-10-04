package com.algocoach.exception;

public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String message, String field) {
        super(String.format("Validation error for field '%s': %s", field, message), 
              "VALIDATION_ERROR", 400);
    }
}
