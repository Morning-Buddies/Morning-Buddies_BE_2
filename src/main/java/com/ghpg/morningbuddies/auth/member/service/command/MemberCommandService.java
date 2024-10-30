package com.ghpg.morningbuddies.auth.member.service.command;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;

public interface MemberCommandService {
	MemberResponseDto.MemberInfo join(MemberRequestDto.JoinDto joinDto);

	void changePassword(MemberRequestDto.PasswordDto request);

	void updateFcmToken(MemberRequestDto.FcmTokenDto request);

	// 그룹 탈퇴
	void leaveGroup(Long groupId);
}
