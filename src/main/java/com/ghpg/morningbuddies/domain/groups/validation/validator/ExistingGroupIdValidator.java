package com.ghpg.morningbuddies.domain.groups.validation.validator;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.domain.groups.validation.annotation.ExistingGroupId;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class ExistingGroupIdValidator implements ConstraintValidator<ExistingGroupId, Long> {

	@Override
	public boolean isValid(Long groupId, ConstraintValidatorContext context) {
		boolean isValid = groupId != null && groupId > 0;

		if (!isValid) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ErrorStatus.GROUP_NOT_FOUND.getMessage())
				.addConstraintViolation();

			return false;
		}

		return true;
	}
}
