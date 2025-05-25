package com.skumar.user_service.validation;

import com.skumar.user_service.dto.PasswordUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordUpdateRequest> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(PasswordUpdateRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.getNewPassword() != null && value.getNewPassword().equals(value.getConfirmPassword());
    }
}