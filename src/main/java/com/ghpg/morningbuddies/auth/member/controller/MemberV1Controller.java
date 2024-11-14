package com.ghpg.morningbuddies.auth.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.command.MemberCommandService;
import com.ghpg.morningbuddies.auth.member.service.query.MemberQueryService;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
@Tag(name = "Member", description = "회원 관련 API")
public class MemberV1Controller {

	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;

	@Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
	@PatchMapping("/me/password")
	public CommonResponse<Void> changePassword(@Valid @RequestBody MemberRequestDto.PasswordDto request) {

		return CommonResponse.onSuccess(memberCommandService.changePassword(request));
	}

	@Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "내 정보 조회 성공", content = {
		@Content(schema = @Schema(implementation = MemberResponseDto.MemberInfo.class))
	})
	@GetMapping("/me")
	public CommonResponse<MemberResponseDto.MemberInfo> getMyInfo() {
		return CommonResponse.onSuccess(memberQueryService.getMyInfo());
	}

	@Operation(summary = "내 그룹 조회", description = "내가 속한 그룹을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "내 그룹 조회 성공", content = {
		@Content(schema = @Schema(implementation = GroupResponseDto.GroupListResponseDTO.class))
	})
	@GetMapping("/me/groups")
	public CommonResponse<GroupResponseDto.GroupListResponseDTO> getMyGroups() {
		return CommonResponse.onSuccess(memberQueryService.getMyGroups());
	}

	@Operation(summary = "FCM 토큰 등록", description = "FCM 토큰을 등록합니다.")
	@ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공", content = {
		@Content(schema = @Schema(implementation = String.class))
	})
	@PostMapping("/me/fcm-token")
	public CommonResponse<Void> updateFcmToken(@Valid @RequestBody MemberRequestDto.FcmTokenDto request) {

		return CommonResponse.onSuccess(memberCommandService.updateFcmToken(request));
	}

	// 회원이 가입한 채팅방 리스트 가져오기
	@GetMapping("/me/{memberId}/chatRooms")
	@Operation(summary = "회원이 가입한 채팅방 리스트 가져오기", description = "회원이 가입한 채팅방 리스트를 가져옵니다.")
	public CommonResponse<List<ChatRoomResponseDto.AllChatRoomByMemberId>> findAllChatroomsByMemberId(
		@PathVariable("memberId") Long memberId) {
		List<ChatRoomResponseDto.AllChatRoomByMemberId> chatRooms = memberQueryService.findAllChatroomsByMemberId(
			memberId);
		return CommonResponse.onSuccess(chatRooms);

	}
}
