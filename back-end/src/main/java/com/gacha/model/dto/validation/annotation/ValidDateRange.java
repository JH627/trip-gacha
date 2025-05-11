package com.gacha.model.dto.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.gacha.model.dto.validation.validator.ValidDateRangeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 시작날짜와 종료날짜가 올바른지 검사
 */
@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "시작일은 종료일보다 이후일 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

