package com.ghpg.morningbuddies.auth.member.validation.validator;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.member.validation.annotation.ValidRefreshToken;
import com.ghpg.morningbuddies.auth.refreshtoken.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.refreshtoken.repository.RefreshTokenJPARepository;
import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenValidator implements ConstraintValidator<ValidRefreshToken, String> {

	private final JwtUtil jwtUtil;
	private final RefreshTokenJPARepository refreshTokenJPARepository;
	private final MemberJPARepository memberJPARepository;

	@Override
	public boolean isValid(String refreshToken, ConstraintValidatorContext context) {
		if (refreshToken == null) {
			setCustomMessage(context, ErrorStatus.INVALID_TOKEN.getMessage());
			return false;
		}

		try {
			validateRefreshToken(refreshToken);
			return true;
		} catch (Exception e) {
			setCustomMessage(context, ErrorStatus.INVALID_TOKEN.getMessage());
			return false;
		}
	}

	private void validateRefreshToken(String refreshToken) {
		// JWT 토큰 유효성 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new GeneralException(ErrorStatus.INVALID_TOKEN);
		}

		// DB에서 리프레시 토큰 조회
		RefreshToken storedToken = refreshTokenJPARepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_TOKEN));

		// 토큰에서 직접 이메일 추출
		String emailFromToken = jwtUtil.getEmail(refreshToken);

		// 토큰의 이메일과 저장된 토큰의 이메일이 일치하는지 확인
		if (!storedToken.getEmail().equals(emailFromToken)) {
			throw new GeneralException(ErrorStatus.INVALID_TOKEN);
		}

		// 사용자 존재 여부 확인
		memberJPARepository.findByEmail(emailFromToken)
			.orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
	}

	private void setCustomMessage(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}