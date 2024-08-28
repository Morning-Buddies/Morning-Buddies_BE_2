package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;

public interface MemberQueryService {
    MemberResponseDto.MemberInfo getMemberInfo();
}
