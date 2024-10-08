package com.ghpg.morningbuddies.global.security.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.dto.CustomMemberDetails;
import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	//JWTUtil 주입
	private final JwtUtil jwtUtil;

	private final ObjectMapper objectMapper;

	private final RefreshTokenRepository refreshRepository;

	private final MemberRepository memberRepository;

	@SneakyThrows
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		// 요청에서 email과 password를 가져옴
		MemberRequestDto.LoginDto loginDto = objectMapper.readValue(request.getInputStream(),
			MemberRequestDto.LoginDto.class
		);

		String email = loginDto.getEmail();
		String password = loginDto.getPassword();

		log.info("Attempting authentication for email: {}", email);

		//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

		//token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	@Transactional
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {
		CustomMemberDetails customMemberDetails = (CustomMemberDetails)authentication.getPrincipal();

		String email = customMemberDetails.getEmail();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority();

		//토큰 생성
		String access = jwtUtil.createJwt("access", email, role, 600000L);
		String refresh = jwtUtil.createJwt("refresh", email, role, 86400000L);

		//refresh을 데이터베이스에 저장
		addRefreshEntity(email, refresh, 86400000L);

		//응답 설정
		response.setHeader("access", access);
		response.addCookie(createCookie("refresh", refresh));
		response.setStatus(HttpStatus.OK.value());

		CommonResponse<String> successResponse = CommonResponse.onSuccess("AUTH_SUCCESS");

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(successResponse));
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		CommonResponse<String> failureResponse = CommonResponse.onFailure("AUTH_FAILED", "Authentication failed", null);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(failureResponse));
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

	private void addRefreshEntity(String email, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		refreshRepository.deleteByEmail(email);

		RefreshToken refreshToken = RefreshToken.builder()
			.member(member)
			.email(email)
			.refresh(refresh)
			.expiration(date.toString())
			.build();

		refreshRepository.save(refreshToken);
	}
}