package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;

public interface MemberCommandService {
	void join(MemberRequestDto.JoinDto joinDto);

	void changePassword(MemberRequestDto.PasswordDto request);
	
	void updateFcmToken(MemberRequestDto.FcmTokenDto request);

	// 그룹 탈퇴
	void leaveGroup(Long groupId);
}
