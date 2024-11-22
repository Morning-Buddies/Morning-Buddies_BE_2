package com.ghpg.morningbuddies.auth.member.validation.validator;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.member.validation.annotation.ValidRefreshToken;
import com.ghpg.morningbuddies.auth.refreshtoken.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.refreshtoken.repository.RefreshTokenJPARepository;
import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.jwt.JwtException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
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
		} catch (JwtException e) {
			setCustomMessage(context, ErrorStatus.INVALID_TOKEN.getMessage());
			return false;
		} catch (GeneralException e) {
			setCustomMessage(context, e.getErrorReason().getMessage());
			return false;
		} catch (Exception e) {
			log.error("Unexpected error during validation", e);
			setCustomMessage(context, ErrorStatus._INTERNAL_SERVER_ERROR.getMessage());
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

		// 현재 인증된 사용자 이메일 가져오기
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();

		// 현재 사용자 정보 조회
		memberJPARepository.findByEmail(currentUserEmail)
			.orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

		// 토큰 소유자와 현재 사용자가 일치하는지 확인
		if (!storedToken.getEmail().equals(currentUserEmail)) {
			throw new GeneralException(ErrorStatus.INVALID_TOKEN);
		}
	}

	private void setCustomMessage(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}