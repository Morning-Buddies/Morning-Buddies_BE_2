package com.ghpg.morningbuddies.auth.member.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ghpg.morningbuddies.auth.member.validation.validator.UniqueEmailValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

	String message() default "이미 존재하는 이메일입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}