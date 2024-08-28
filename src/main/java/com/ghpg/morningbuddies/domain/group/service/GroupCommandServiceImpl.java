package com.ghpg.morningbuddies.domain.group.service;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.domain.file.service.FileCommandService;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupCommandServiceImpl implements GroupCommandService {

    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;
    private final FileCommandService fileCommandService;

    // 그룹 생성
    @Override
    public GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.CreateGroupDto requestDto, MultipartFile file) {

        String currentEmail = SecurityUtil.getCurrentMemberEmail();
        Member leader = memberRepository.findByEmail(currentEmail)
                        .orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_ALREADY_EXIST));

        Optional<Groups> existingGroup = groupRepository.findByGroupName(requestDto.getGroupName());
        if (existingGroup.isPresent()) {
            throw new GroupException(GlobalErrorCode.GROUP_ALREADY_CREATED);
        }

        String uploadedGroupImageUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                uploadedGroupImageUrl = fileCommandService.saveFile(file);
            } catch (Exception e) {
                throw new GroupException(GlobalErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        Groups group = Groups.builder()
                .groupName(requestDto.getGroupName())
                .description(requestDto.getDescription())
                .wakeupTime(requestDto.getWakeUpTime())
                .currentParticipantCount(1)
                .leader(leader)
                .maxParticipantCount(requestDto.getMaxParticipantCount())
                .isActivated(true)
                .groupImage(uploadedGroupImageUrl)
                .build();

        group.addMember(leader);

        Groups savedGroup = groupRepository.save(group);

        ArrayList<Member> members = new ArrayList<>();
        members.add(leader);

        return GroupResponseDto.GroupDetailDTO.builder()
                .groupId(savedGroup.getId())
                .groupName(savedGroup.getGroupName())
                .wakeUpTime(savedGroup.getWakeupTime())
                .currentParticipantCount(savedGroup.getCurrentParticipantCount())
                .maxParticipantCount(requestDto.getMaxParticipantCount() != null ? requestDto.getMaxParticipantCount() : 0) // Null 체크
                .description(savedGroup.getDescription())
                .imageUrl(savedGroup.getGroupImage())
                .members(members.stream().map(MemberResponseDto.MemberSummaryDTO::from).collect(Collectors.toList()))
                .leader(GroupResponseDto.LeaderDTO.from(savedGroup.getLeader()))
                .build();

    }
}
