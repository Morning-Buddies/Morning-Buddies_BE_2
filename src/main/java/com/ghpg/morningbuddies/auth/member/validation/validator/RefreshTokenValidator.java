package com.ghpg.morningbuddies.auth.member.validation.validator;

import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.auth.member.validation.annotation.ValidRefreshToken;
import com.ghpg.morningbuddies.global.exception.JwtException.JwtException;
import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
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
	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;

	@Override
	public boolean isValid(String refreshToken, ConstraintValidatorContext context) {
		if (refreshToken == null) {
			setCustomMessage(context, GlobalErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
			return false;
		}

		try {
			validateRefreshToken(refreshToken);
			return true;
		} catch (JwtException e) {
			setCustomMessage(context, GlobalErrorCode.INVALID_REFRESH_TOKEN.getMessage());
			return false;
		} catch (GeneralException e) {
			setCustomMessage(context, e.getErrorCode().getReason().getMessage());
			return false;
		} catch (Exception e) {
			log.error("Unexpected error during validation", e);
			setCustomMessage(context, GlobalErrorCode.SERVER_ERROR.getMessage());
			return false;
		}
	}

	private void validateRefreshToken(String refreshToken) {
		// JWT 토큰 유효성 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new GeneralException(GlobalErrorCode.INVALID_REFRESH_TOKEN);
		}

		// DB에서 리프레시 토큰 조회
		RefreshToken storedToken = refreshTokenRepository.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new GeneralException(GlobalErrorCode.INVALID_REFRESH_TOKEN));

		// 현재 인증된 사용자 이메일 가져오기
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();

		// 현재 사용자 정보 조회
		memberRepository.findByEmail(currentUserEmail)
			.orElseThrow(() -> new GeneralException(GlobalErrorCode.MEMBER_NOT_FOUND));

		// 토큰 소유자와 현재 사용자가 일치하는지 확인
		if (!storedToken.getEmail().equals(currentUserEmail)) {
			throw new GeneralException(GlobalErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	private void setCustomMessage(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message)
			.addConstraintViolation();
	}
}