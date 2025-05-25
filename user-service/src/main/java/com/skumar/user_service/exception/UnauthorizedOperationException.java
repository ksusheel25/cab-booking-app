package com.skumar.user_service.exception;

public class UnauthorizedOperationException extends UserServiceException {
    public UnauthorizedOperationException(String message) {
        super("Unauthorized operation: " + message);
    }
}