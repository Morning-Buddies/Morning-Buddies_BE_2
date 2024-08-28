package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;

public interface MemberCommandService {
    void join(MemberRequestDto.JoinDto joinDto);

    void changePassword(MemberRequestDto.PasswordDto request);
}
