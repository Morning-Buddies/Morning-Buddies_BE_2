package com.ghpg.morningbuddies.auth.member.validation.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.member.validation.annotation.UniquePhoneNumber;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Component
@RequiredArgsConstructor
public class UniquePhoneNumberValidator implements ConstraintValidator<UniquePhoneNumber, String> {

	private final MemberJPARepository memberJPARepository;

	@Override
	public void initialize(UniquePhoneNumber constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}

	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
		Optional<Member> existingMember = memberJPARepository.findByPhoneNumber(phoneNumber);

		if (existingMember.isPresent()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					GlobalErrorCode.MEMBER_ALREADY_EXIST.getMessage())
				.addConstraintViolation();

			// ValidationException을 던지지 않고 false 반환
			return false;
		}

		return true;
	}
}
