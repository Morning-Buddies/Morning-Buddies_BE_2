package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;

public interface MemberCommandService {
    void join(MemberRequestDto.JoinDto joinDto);
}
