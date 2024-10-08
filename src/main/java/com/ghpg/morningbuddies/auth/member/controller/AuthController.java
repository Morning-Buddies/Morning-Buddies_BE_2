package com.ghpg.morningbuddies.auth.member.controller;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.auth.member.service.MemberCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth API", description = "인증.인가 API")
public class AuthController {

	private final JwtUtil jwtUtil;

	private final MemberCommandService memberCommandService;

	private final RefreshTokenRepository refreshRepository;

	private final MemberRepository memberRepository;

	@GetMapping("/currentMember")
	@Operation(summary = "현재 로그인한 사용자 정보 조회", description = "현재 로그인한 사용자의 이메일을 조회합니다.")
	public CommonResponse<String> currentMember() {

		String currentMember = SecurityUtil.getCurrentMemberEmail();

		return CommonResponse.onSuccess(currentMember);

	}

	@PostMapping("/join")
	@Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
	public CommonResponse<String> join(@Valid @RequestBody MemberRequestDto.JoinDto request) {

		memberCommandService.join(request);

		return CommonResponse.onSuccess("Join Success");
	}

	@PostMapping("/reissue")
	@Operation(summary = "토큰 재발급", description = "Access Token이 만료되었을 때, Refresh Token을 이용하여 새로운 Access Token을 발급합니다.")
	public ResponseEntity<CommonResponse<String>> reissue(HttpServletRequest request, HttpServletResponse response) {

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refresh")) {

				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			return ResponseEntity.badRequest().body(CommonResponse
				.onFailure(GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getCode(),
					GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getMessage(),
					null));
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			//response status code
			return ResponseEntity.badRequest().body(CommonResponse
				.onFailure(GlobalErrorCode.REFRESH_TOKEN_EXPIRED.getCode(),
					GlobalErrorCode.REFRESH_TOKEN_EXPIRED.getMessage(),
					null));
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {

			//response status code
			return ResponseEntity.badRequest().body(CommonResponse
				.onFailure(GlobalErrorCode.INVALID_REFRESH_TOKEN.getCode(),
					GlobalErrorCode.INVALID_REFRESH_TOKEN.getMessage(),
					null));
		}

		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);

		//make new JWT
		String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
		String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

		//delete old refresh token
		refreshRepository.deleteByRefresh(refresh);

		//add new refresh token
		addRefreshEntity(username, newRefresh, 86400000L);

		//response
		response.setHeader("access", newAccess);
		response.addCookie(createCookie("refresh", newRefresh));

		return ResponseEntity.ok(CommonResponse.onSuccess("Reissue Success"));
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

		RefreshToken refreshToken = RefreshToken.builder()
			.member(member)
			.email(email)
			.refresh(refresh)
			.expiration(date.toString())
			.build();

		refreshRepository.save(refreshToken);
	}
}
