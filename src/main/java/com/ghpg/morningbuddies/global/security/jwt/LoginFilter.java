package com.ghpg.morningbuddies.global.security.jwt;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.dto.CustomUserDetails;
import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.refreshtoken.service.RefreshTokenCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private static final String EMAIL_PARAMETER = "email";
	private static final String COOKIE_NAME = "refresh_token";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final String LOGIN_URL = "/api/v1/auth/login";
	private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final JwtUtil jwtUtil;
	private final RefreshTokenCommandService refreshTokenService;

	public LoginFilter(
		AuthenticationManager authenticationManager,
		ObjectMapper objectMapper,
		JwtUtil jwtUtil,
		RefreshTokenCommandService refreshTokenService) {
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.jwtUtil = jwtUtil;
		this.refreshTokenService = refreshTokenService;
		setUsernameParameter(EMAIL_PARAMETER);
		setFilterProcessesUrl(LOGIN_URL);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {
		try {
			MemberRequestDto.LoginDto loginRequest = parseLoginRequest(request);

			return authenticateUser(loginRequest);
		} catch (IOException e) {
			throw new MemberException(GlobalErrorCode.INVALID_LOGIN_REQUEST);
		}
	}

	private MemberRequestDto.LoginDto parseLoginRequest(HttpServletRequest request) throws IOException {
		return objectMapper.readValue(request.getInputStream(), MemberRequestDto.LoginDto.class);
	}

	private Authentication authenticateUser(MemberRequestDto.LoginDto loginRequest) {
		UsernamePasswordAuthenticationToken authToken =
			new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
		return authenticationManager.authenticate(authToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain, Authentication authentication) throws IOException {
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		Member member = userDetails.getMember();

		String userEmail = userDetails.getUsername();
		String userRole = extractUserRole(userDetails);

		handleTokenGeneration(response, userEmail, userRole);

		writeMemberInfoResponse(response, member);
	}

	private String extractUserRole(CustomUserDetails userDetails) {
		return userDetails.getAuthorities().stream()
			.findFirst()
			.orElseThrow()
			.getAuthority();
	}

	private void handleTokenGeneration(HttpServletResponse response, String userEmail, String userRole) {
		String accessToken = jwtUtil.createAccessToken(userEmail, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userEmail, userRole);

		refreshTokenService.saveNewRefreshToken(userEmail, refreshToken);
		setTokenHeaders(response, accessToken, refreshToken);
	}

	private void setTokenHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
		ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
		response.addHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken);
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
	}

	private ResponseCookie createRefreshTokenCookie(String refreshToken) {
		return ResponseCookie.from(COOKIE_NAME, refreshToken)
			.httpOnly(true)
			.secure(true)
			.sameSite("Strict")
			.path("/")
			.maxAge(REFRESH_TOKEN_DURATION)
			.build();
	}

	private void writeMemberInfoResponse(HttpServletResponse response, Member member) throws IOException {
		MemberResponseDto.MemberInfo memberInfo = MemberResponseDto.MemberInfo.of(member);
		setResponseProperties(response);
		objectMapper.writeValue(response.getOutputStream(), CommonResponse.onSuccess(memberInfo));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		BaseErrorCode errorCode = determineErrorCode(failed);
		writeErrorResponse(response, errorCode, failed);
	}

	private BaseErrorCode determineErrorCode(AuthenticationException exception) {
		if (exception instanceof UsernameNotFoundException ||
			exception.getCause() instanceof MemberException)  // 이 부분 추가
			return GlobalErrorCode.MEMBER_NOT_FOUND;
		if (exception instanceof BadCredentialsException)
			return GlobalErrorCode.INVALID_CREDENTIALS;
		if (exception instanceof DisabledException)
			return GlobalErrorCode.ACCOUNT_DISABLED;
		if (exception instanceof LockedException)
			return GlobalErrorCode.ACCOUNT_LOCKED;
		if (exception instanceof AccountExpiredException)
			return GlobalErrorCode.ACCOUNT_EXPIRED;
		return GlobalErrorCode.LOGIN_FAILED;
	}

	private void writeErrorResponse(HttpServletResponse response, BaseErrorCode errorCode,
		AuthenticationException exception) throws IOException {
		setResponseProperties(response);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		CommonResponse<?> errorResponse = CommonResponse.onFailure(
			errorCode.getReason().getCode(),
			errorCode.getReason().getMessage(),
			exception.getMessage()
		);

		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}

	private void setResponseProperties(HttpServletResponse response) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}
}