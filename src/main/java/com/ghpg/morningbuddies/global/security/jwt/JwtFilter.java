package com.ghpg.morningbuddies.global.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.dto.CustomUserDetails;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.service.query.CustomUserDetailsService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.ErrorReason;
import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.jwt.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final CustomUserDetailsService customUserDetailsService;

	private static final String JOIN_URL = "/api/v1/auth/join";
	private static final String LOGIN_URL = "/api/v1/auth/login";
	private static final String REISSUE_URL = "/api/v1/auth/reissue";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String authorization = request.getHeader("Authorization");

		// 회원가입, 로그인 요청인 경우
		if (request.getRequestURI().equals(JOIN_URL) || request.getRequestURI().equals(LOGIN_URL)) {
			filterChain.doFilter(request, response);
			return;
		}

		// Refresh Token 재발급 요청인 경우
		if (request.getRequestURI().equals(REISSUE_URL)) {
			filterChain.doFilter(request, response);
			return;
		}

		// Authorization 헤더가 없거나 Bearer로 시작하지 않는 경우
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// Bearer 토큰에서 JWT 추출
			String token = jwtUtil.extractToken(authorization);

			// 토큰 유효성 검증
			jwtUtil.validateToken(token);

			// 토큰에서 email과 role 추출
			String email = jwtUtil.getEmail(token);
			String role = jwtUtil.getRole(token);

			if (email != null) {
				// UserDetails 로드
				CustomUserDetails customUserDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(
					email);
				Member member = customUserDetails.getMember();

				// Role 검증
				if (!member.getRole().name().equals(role)) {
					handleException(new GeneralException(ErrorStatus.AUTHENTICATION_DENIED), response);
					return;
				}

				// Authentication 객체 생성 및 설정
				Authentication auth = new UsernamePasswordAuthenticationToken(
					customUserDetails,
					null,
					customUserDetails.getAuthorities()
				);
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

		} catch (JwtException e) {
			handleException(new GeneralException(e.getCode()), response);
			return;
		} catch (Exception e) {
			log.error("JWT Filter Error", e);
			handleException(new GeneralException(ErrorStatus.INVALID_TOKEN), response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private void handleException(GeneralException ex, HttpServletResponse response) throws IOException {
		ErrorReason errorReason = ex.getErrorReasonHttpStatus();

		response.setStatus(errorReason.getHttpStatus().value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		CommonResponse<?> errorResponse = CommonResponse.onFailure(
			errorReason.getCode(),
			errorReason.getMessage(),
			null
		);

		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}
}
