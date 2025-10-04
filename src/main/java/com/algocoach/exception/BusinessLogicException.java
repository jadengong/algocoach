package com.algocoach.exception;

public class BusinessLogicException extends CustomException {
    public BusinessLogicException(String message) {
        super(message, "BUSINESS_LOGIC_ERROR", 400);
    }

    public BusinessLogicException(String message, String errorCode) {
        super(message, errorCode, 400);
    }
}
