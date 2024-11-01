package com.ghpg.morningbuddies.auth.member.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ghpg.morningbuddies.auth.member.validation.validator.RefreshTokenValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = RefreshTokenValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRefreshToken {

	String message() default "유효하지 않은 토큰입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
