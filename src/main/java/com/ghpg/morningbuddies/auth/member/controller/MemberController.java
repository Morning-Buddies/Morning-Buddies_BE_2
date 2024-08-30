package com.ghpg.morningbuddies.auth.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.MemberCommandService;
import com.ghpg.morningbuddies.auth.member.service.MemberQueryService;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

	private final MemberQueryService memberQueryService;

	private final MemberCommandService memberCommandService;

	@GetMapping("/me")
	public CommonResponse<MemberResponseDto.MemberInfo> getMemberInfo(
		@CookieValue(name = "refresh") String refreshToken) {
		if (refreshToken == null) {
			return CommonResponse.onFailure(GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getCode(),
				GlobalErrorCode.REFRESH_TOKEN_REQUIRED.getMessage(), null);
		}
		log.info("refreshToken: {}", refreshToken);

		return CommonResponse.onSuccess(memberQueryService.getMemberInfo(refreshToken));
	}

	@PatchMapping("/me/password")
	public CommonResponse<String> changePassword(@Valid @RequestBody MemberRequestDto.PasswordDto request) {
		memberCommandService.changePassword(request);

		return CommonResponse.onSuccess("비밀번호 변경 성공");
	}

	@GetMapping("/me/groups")
	public CommonResponse<List<GroupResponseDto.GroupInfo>> getMyGroups() {
		return CommonResponse.onSuccess(memberQueryService.getMyGroups());
	}

	@PostMapping("/me/fcm-token")
	public CommonResponse<String> registerFcmToken(@Valid @RequestBody MemberRequestDto.FcmTokenDto request) {
		memberCommandService.registerFcmToken(request);

		return CommonResponse.onSuccess("FCM 토큰 등록 성공");
	}
}
