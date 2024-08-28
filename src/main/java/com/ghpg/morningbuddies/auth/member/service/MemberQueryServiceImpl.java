package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService{
    @Override
    public MemberResponseDto.MemberInfo getMemberInfo() {
        return null;
    }
}
