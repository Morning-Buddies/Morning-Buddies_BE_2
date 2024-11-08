package com.ghpg.morningbuddies.auth.member.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.command.MemberCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "회원 인증/인가 관리 API")
@RestController
@RequestMapping("/auth")
@SecurityRequirements
@RequiredArgsConstructor
public class AuthController {

	private final MemberCommandService memberCommandService;

	/**
	 * 회원 가입
	 * @param joinDto
	 * @return
	 */
	@Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 가입 성공",
			content = @Content(schema = @Schema(implementation = MemberResponseDto.MemberInfo.class)))
	})
	@PostMapping("/join")
	public CommonResponse<MemberResponseDto.MemberInfo> join(
		@RequestBody @Valid MemberRequestDto.JoinDto joinDto
	) {

		return CommonResponse.onSuccess(memberCommandService.join(joinDto));
	}

	/**
	 * 로그인
	 * @param loginDto
	 * @return
	 */
	@Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인합니다.")
	@ApiResponse(responseCode = "200", description = "로그인 성공",
		content = @Content(schema = @Schema(implementation = MemberResponseDto.MemberInfo.class)))
	@PostMapping("/login")
	public CommonResponse<MemberResponseDto.MemberInfo> login(
		@RequestBody @Valid MemberRequestDto.LoginDto loginDto
	) {
		return null;
	}

	@Operation(summary = "로그아웃", description = "로그아웃합니다.")
	@ApiResponse(responseCode = "200", description = "로그아웃 성공",
		content = @Content(schema = @Schema(implementation = Void.class)))
	@PostMapping("/logout")
	public CommonResponse<Void> logout() {
		return null;
	}

	@Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.")
	@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
		content = @Content(schema = @Schema(implementation = Void.class)))
	@PostMapping("/withdraw")
	public CommonResponse<Void> withdraw() {
		return CommonResponse.onSuccess(memberCommandService.withdraw());
	}

}
