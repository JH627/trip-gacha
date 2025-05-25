package com.gacha.model.dto.validation.validator;

import org.springframework.stereotype.Component;

import com.gacha.model.dto.validation.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private String message;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // 8자 이상
        if (value.length() < 8) {
            return false;
        }

        // 대문자 포함
        boolean hasUpperCase = value.matches(".*[A-Z].*");
        // 소문자 포함
        boolean hasLowerCase = value.matches(".*[a-z].*");
        // 숫자 포함
        boolean hasDigit = value.matches(".*\\d.*");
        // 특수문자 포함
        boolean hasSpecial = value.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        boolean isValid = hasUpperCase && hasLowerCase && hasDigit && hasSpecial;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }
} 