package com.ghpg.morningbuddies.auth.member.validation.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.member.validation.annotation.UniqueEmail;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

	private final MemberJPARepository memberJPARepository;

	@Override
	public void initialize(UniqueEmail constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		Optional<Member> existingMember = memberJPARepository.findByEmail(email);

		if (existingMember.isPresent()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					ErrorStatus.MEMBER_ALREADY_EXIST.getCode())
				.addConstraintViolation();

			// ValidationException을 던지지 않고 false 반환
			return false;
		}

		return true;
	}
}