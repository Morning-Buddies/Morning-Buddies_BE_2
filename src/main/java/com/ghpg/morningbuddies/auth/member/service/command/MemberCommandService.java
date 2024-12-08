package com.ghpg.morningbuddies.auth.member.service.command;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDTO;
import com.ghpg.morningbuddies.auth.member.dto.TokenDto;

public interface MemberCommandService {
	MemberResponseDTO.MemberInfo join(MemberRequestDto.JoinDto joinDto);

	Void changePassword(MemberRequestDto.PasswordDto request);

	Void withdraw();

	Void updateFcmToken(MemberRequestDto.FcmTokenDto request);

	// 그룹 탈퇴
	void leaveGroup(Long groupId);

	TokenDto.ReissueDto reissue(String oldRefreshToken);

}
