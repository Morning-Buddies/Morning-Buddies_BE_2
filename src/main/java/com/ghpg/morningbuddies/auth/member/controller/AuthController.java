package com.ghpg.morningbuddies.auth.member.controller;

import org.springframework.web.ErrorResponse;
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

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequestMapping("/auth")
@SecurityRequirements
@RequiredArgsConstructor
public class AuthController {

	private final MemberCommandService memberCommandService;

	@Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "회원 가입 성공",
			content = @Content(schema = @Schema(implementation = MemberResponseDto.MemberInfo.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 회원",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/join")
	public CommonResponse<MemberResponseDto.MemberInfo> join(
		@RequestBody @Valid MemberRequestDto.JoinDto joinDto
	) {
		// 회원가입 로직

		return CommonResponse.onSuccess(memberCommandService.join(joinDto));
	}

	@Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그인 성공",
			content = @Content(schema = @Schema(implementation = MemberResponseDto.MemberInfo.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	@PostMapping("/login")
	public CommonResponse<MemberResponseDto.MemberInfo> login(
		@RequestBody @Valid MemberRequestDto.LoginDto loginDto
	) {
		return null;
	}

}
