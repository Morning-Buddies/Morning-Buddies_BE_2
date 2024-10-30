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
import com.ghpg.morningbuddies.auth.member.service.RefreshTokenService;
import com.ghpg.morningbuddies.domain.group.mapper.GroupMapper;
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

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;

	//생성자 주입
	public LoginFilter(
		AuthenticationManager authenticationManager,
		ObjectMapper objectMapper,
		JwtUtil jwtUtil,
		RefreshTokenService refreshTokenService) {
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.jwtUtil = jwtUtil;
		this.refreshTokenService = refreshTokenService;
		this.setUsernameParameter("email");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {

		try {
			// JSON 요청을 처리하기 위한 LoginRequest DTO 사용
			MemberRequestDto.LoginDto loginRequest = objectMapper.readValue(request.getInputStream(),
				MemberRequestDto.LoginDto.class);

			// email과 password로 인증 토큰 생성
			UsernamePasswordAuthenticationToken authToken =
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), null);

			// AuthenticationManager로 인증 처리
			return authenticationManager.authenticate(authToken);

		} catch (IOException e) {
			throw new MemberException(GlobalErrorCode.INVALID_LOGIN_REQUEST);
		}
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {

		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();
		Member member = customUserDetails.getMember();

		String userEmail = customUserDetails.getUsername();
		String userRole = customUserDetails.getAuthorities().stream()
			.findFirst()
			.orElseThrow()
			.getAuthority();

		String accessToken = jwtUtil.createAccessToken(userEmail, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userEmail, userRole);

		refreshTokenService.saveNewRefreshToken(userEmail, refreshToken);

		// 쿠키로 refresh_token 설정
		ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
			.httpOnly(true)
			.secure(true)
			.sameSite("Strict")
			.path("/")
			.maxAge(Duration.ofDays(14))
			.build();

		MemberResponseDto.MemberInfo memberInfo = MemberResponseDto.MemberInfo.builder()
			.id(member.getId())
			.profileImage(member.getProfileImageUrl())
			.firstName(member.getFirstName())
			.lastName(member.getLastName())
			.preferredWakeupTime(member.getPreferredWakeupTime())
			.groups(member.getGroups().stream().map(GroupMapper::toGroupInfo).toList())
			.successGameCount(GroupMapper.getCountSuccessGame(member))
			.build();

		// Bearer 토큰 헤더 설정
		response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		objectMapper.writeValue(response.getOutputStream(), CommonResponse.onSuccess(
			memberInfo
		));
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseErrorCode errorCode;

		// 예외 종류에 따른 에러 코드 설정
		if (failed instanceof BadCredentialsException) {
			errorCode = GlobalErrorCode.INVALID_CREDENTIALS;
		} else if (failed instanceof UsernameNotFoundException) {
			errorCode = GlobalErrorCode.MEMBER_NOT_FOUND;
		} else if (failed instanceof DisabledException) {
			errorCode = GlobalErrorCode.ACCOUNT_DISABLED;
		} else if (failed instanceof LockedException) {
			errorCode = GlobalErrorCode.ACCOUNT_LOCKED;
		} else if (failed instanceof AccountExpiredException) {
			errorCode = GlobalErrorCode.ACCOUNT_EXPIRED;
		} else {
			errorCode = GlobalErrorCode.LOGIN_FAILED;
		}

		CommonResponse<?> errorResponse = CommonResponse.onFailure(
			errorCode.getReason().getCode(),
			errorCode.getReason().getMessage(),
			failed.getMessage()
		);

		objectMapper.writeValue(response.getOutputStream(), errorResponse);
	}

}