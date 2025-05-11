package com.gacha.model.dto.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import com.gacha.model.dto.validation.validator.ValidAddressValidator;

@Documented
@Constraint(validatedBy = ValidAddressValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAddress {
    String message() default "올바르지 않은 주소 형식입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
