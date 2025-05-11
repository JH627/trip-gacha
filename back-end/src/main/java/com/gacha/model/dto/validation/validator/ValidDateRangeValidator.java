package com.gacha.model.dto.validation.validator;

import org.springframework.stereotype.Component;

import com.gacha.model.dto.request.TripRequest.ScheduleRegistForm;
import com.gacha.model.dto.validation.annotation.ValidDateRange;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, ScheduleRegistForm> {

    private String message;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }
	
    @Override
    public boolean isValid(ScheduleRegistForm dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return true; 
        }
        
        boolean isValid = !dto.getStartDate().isAfter(dto.getEndDate());
        if (!isValid) {
            context.disableDefaultConstraintViolation(); // 기본 오류 메시지 비활성화
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation(); // 커스텀 메세지로 변경
        }
        
        return isValid;
    }
}

