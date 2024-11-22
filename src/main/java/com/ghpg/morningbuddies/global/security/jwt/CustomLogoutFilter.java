package com.ghpg.morningbuddies.global.security.jwt;

import java.io.IOException;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.refreshtoken.service.RefreshTokenCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
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
public class CustomLogoutFilter extends OncePerRequestFilter {

	private static final String LOGOUT_URL = "/api/v1/auth/logout";
	private static final String COOKIE_NAME = "refresh_token";

	private final JwtUtil jwtUtil;
	private final RefreshTokenCommandService refreshTokenService;
	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		if (isLogoutRequest(request)) {
			handleLogout(request, response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isLogoutRequest(HttpServletRequest request) {
		return request.getRequestURI().equals(LOGOUT_URL) &&
			request.getMethod().equals("POST");
	}

	private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			// Extract both tokens
			String accessToken = jwtUtil.extractAccessToken(request);
			String refreshToken = jwtUtil.extractRefreshToken(request);

			// Validate access token
			if (!jwtUtil.validateToken(accessToken) || !jwtUtil.isAccessToken(accessToken)) {
				throw new JwtException(ErrorStatus.INVALID_TOKEN);
			}

			// Get user email from access token
			String userEmail = jwtUtil.getEmail(accessToken);

			// Perform logout operations
			performLogout(response, refreshToken);
			writeSuccessResponse(response);

		} catch (JwtException e) {
			writeErrorResponse(response, e);
		} catch (Exception e) {
			writeErrorResponse(response, new JwtException(ErrorStatus.LOGOUT_FAILED));
		}
	}

	private void performLogout(HttpServletResponse response, String refreshToken) {
		// Remove refresh token from database if exists
		if (refreshToken != null) {
			try {
				if (jwtUtil.validateToken(refreshToken) && jwtUtil.isRefreshToken(refreshToken)) {
					refreshTokenService.removeRefreshToken(refreshToken);
				}
			} catch (Exception e) {
				log.error("Failed to remove refresh token from database: {}", e.getMessage());
			}
		}

		// Clear refresh token cookie
		ResponseCookie clearCookie = ResponseCookie.from(COOKIE_NAME, "")
			.httpOnly(true)
			.secure(true)
			.sameSite("Strict")
			.path("/")
			.maxAge(0)
			.build();

		response.addHeader("Set-Cookie", clearCookie.toString());

		// Clear security context
		SecurityContextHolder.clearContext();
	}

	private void writeSuccessResponse(HttpServletResponse response) throws IOException {
		setResponseProperties(response);
		CommonResponse<?> successResponse = CommonResponse.onSuccess("로그아웃이 성공적으로 완료되었습니다.");
		objectMapper.writeValue(response.getOutputStream(), successResponse);
	}

	private void writeErrorResponse(HttpServletResponse response, Exception exception) throws IOException {
		setResponseProperties(response);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		String code = ErrorStatus.LOGOUT_FAILED.getReason().getCode();
		String message = ErrorStatus.LOGOUT_FAILED.getReason().getMessage();

		if (exception instanceof JwtException) {
			JwtException jwtException = (JwtException)exception;
			code = jwtException.getCode().getReason().getCode();
			message = jwtException.getCode().getReason().getMessage();
		}

		CommonResponse<?> errorResponse = CommonResponse.onFailure(
			code,
			message,
			exception.getMessage()
		);

		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}

	private void setResponseProperties(HttpServletResponse response) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}
}