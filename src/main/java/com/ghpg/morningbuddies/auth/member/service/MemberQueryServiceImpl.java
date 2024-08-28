package com.ghpg.morningbuddies.auth.member.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.domain.group.Groups;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.refresh.RefreshException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService{

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public MemberResponseDto.MemberInfo getMemberInfo(String refreshToken) {
        Member foundMember = refreshTokenRepository.findByRefreshWithMemberAndGroup(refreshToken)
                .orElseThrow(() -> new RefreshException(GlobalErrorCode.INVALID_TOKEN))
                .getMember();

        List<Groups> foundGroups = foundMember.getGroups();


        // 그룹 총 성공 횟수를 멤버의 회원 정보에 저장
        int totalSuccessCount = Optional.ofNullable(foundGroups)
                .map(groups -> groups.stream()
                        .mapToInt(Groups::getSuccessCount)
                        .sum())
                .orElse(0);

        // 그룹 정보 저장
        List<MemberResponseDto.GroupInfo> groupInfos = new ArrayList<>();

        // 그룹 정보가 존재할 경우 그룹 정보 저장
        if (foundGroups != null) {
            for (Groups foundGroup : foundGroups) {
                groupInfos.add(MemberResponseDto.GroupInfo.builder()
                        .name(foundGroup.getName())
                        .wakeupTime(foundGroup.getWakeupTime())
                        .build());
            }
        }


        return MemberResponseDto.MemberInfo.builder()
                .profileImage(foundMember.getProfileImage())
                .firstName(foundMember.getFirstName())
                .lastName(foundMember.getLastName())
                .preferredWakeupTime(foundMember.getPreferredWakeupTime())
                .successGameCount(totalSuccessCount)
                .groups(groupInfos)
                .build();
    }
}
