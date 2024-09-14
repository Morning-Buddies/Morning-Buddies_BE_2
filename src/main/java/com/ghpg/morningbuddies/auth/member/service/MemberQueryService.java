package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

import java.util.List;

public interface MemberQueryService {
    MemberResponseDto.MemberInfo getMemberInfo(String refreshToken);

    List<GroupResponseDto.GroupInfo> getMyGroups();
}
