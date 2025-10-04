package com.algocoach.exception;

public class RateLimitException extends CustomException {
    public RateLimitException(String message) {
        super(message, "RATE_LIMIT_EXCEEDED", 429);
    }
}
