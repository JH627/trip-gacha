package com.gacha.model.dto.validation.validator;

import org.springframework.stereotype.Component;

import com.gacha.model.dto.validation.annotation.ValidAddress;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidAddressValidator implements ConstraintValidator<ValidAddress, String> {

    private String message;

    @Override
    public void initialize(ValidAddress constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // TODO : 유효한 주소인지 검사하는 로직 구현 (Google Maps API)

        boolean isValid = true; // 테스트용. 실제 주소 검증 로직으로 교체해야 함

        if (!isValid) {
            context.disableDefaultConstraintViolation(); // 기본 오류 메시지 비활성화
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation(); // 커스텀 메세지로 변경
        }

        return isValid;
    }
}
