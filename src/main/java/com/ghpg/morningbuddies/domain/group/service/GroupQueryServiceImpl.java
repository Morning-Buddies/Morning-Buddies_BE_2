package com.ghpg.morningbuddies.domain.group.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryServiceImpl implements GroupQueryService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    // 그룹 정보 가져오기
    @Override
    public GroupResponseDto.GroupDetailDTO getGroupDetailById(Long groupId) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

        List<Member> allMemberInGroup = memberRepository.findAllMemberByGroupId(groupId);

        return GroupResponseDto.GroupDetailDTO.builder()
                .groupName(group.getGroupName())
                .wakeUpTime(group.getWakeupTime())
                .currentParticipantCount(group.getCurrentParticipantCount())
                .maxParticipantCount(group.getMaxParticipantCount())
                .description(group.getDescription())
                .imageUrl(group.getGroupImage())
                .leader(GroupResponseDto.LeaderDTO.from(group.getLeader()))
                .members(allMemberInGroup.stream()
                        .map(MemberResponseDto.MemberSummaryDTO::from)
                        .collect(Collectors.toList())).build();
    }

}
