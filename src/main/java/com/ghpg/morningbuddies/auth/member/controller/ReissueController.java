package com.ghpg.morningbuddies.auth.member.controller;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.TokenDto;
import com.ghpg.morningbuddies.auth.member.service.command.MemberCommandService;
import com.ghpg.morningbuddies.auth.member.validation.annotation.ValidRefreshToken;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "회원 인증/인가 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class ReissueController {

	private final MemberCommandService memberCommandService;

	@Operation(summary = "Access Token 재발급", description = "Refresh Token을 사용하여 Access Token을 재발급합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Access Token 재발급 성공",
			content = @Content(schema = @Schema(implementation = String.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "403", description = "Refresh Token 만료",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/reissue")
	public ResponseEntity<CommonResponse<Void>> reissue(
		@CookieValue("refresh_token") @ValidRefreshToken String refreshToken
	) {
		TokenDto.ReissueDto newTokens = memberCommandService.reissue(refreshToken);
		ResponseCookie cookie = ResponseCookie.from("refresh_token", newTokens.getRefreshToken())
			.httpOnly(true)
			.secure(true)
			.sameSite("Strict")
			.path("/")
			.maxAge(Duration.ofDays(14))
			.build();

		return ResponseEntity.ok()
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + newTokens.getAccessToken())
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(CommonResponse.onSuccess(null));  // 응답 본문에는 토큰 정보를 포함하지 않음

	}
}
