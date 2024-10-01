package com.ghpg.morningbuddies.auth.member.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.service.MemberCommandService;
import com.ghpg.morningbuddies.auth.member.service.MemberQueryService;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {

	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
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
	@Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
	public CommonResponse<String> changePassword(@Valid @RequestBody MemberRequestDto.PasswordDto request) {
		memberCommandService.changePassword(request);

		return CommonResponse.onSuccess("비밀번호 변경 성공");
	}

	@GetMapping("/me/groups")
	@Operation(summary = "내 그룹 조회", description = "내가 속한 그룹을 조회합니다.")
	public CommonResponse<List<GroupResponseDto.GroupInfo>> getMyGroups() {
		return CommonResponse.onSuccess(memberQueryService.getMyGroups());
	}

	@PostMapping("/me/fcm-token")
	@Operation(summary = "FCM 토큰 등록", description = "FCM 토큰을 등록합니다.")
	public CommonResponse<String> updateFcmToken(@Valid @RequestBody MemberRequestDto.FcmTokenDto request) {
		memberCommandService.updateFcmToken(request);

		return CommonResponse.onSuccess("FCM 토큰 갱신 성공");
	}

	// 그룹 탈퇴
	@DeleteMapping("/me/groups/{groupId}")
	@Operation(summary = "그룹 탈퇴", description = "그룹에서 탈퇴합니다.")
	public CommonResponse<String> deleteGroup(@PathVariable("groupId") Long groupId) {
		memberCommandService.leaveGroup(groupId);
		return CommonResponse.onSuccess("그룹에서 탈퇴하였습니다.");
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
