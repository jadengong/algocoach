package com.algocoach.exception;

public class AuthenticationException extends CustomException {
    public AuthenticationException(String message) {
        super(message, "AUTHENTICATION_ERROR", 401);
    }
}
