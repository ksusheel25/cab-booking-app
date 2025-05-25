package com.skumar.user_service.exception;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}