package com.ghpg.morningbuddies.domain.group.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ghpg.morningbuddies.auth.member.entity.MemberGroup;
import com.ghpg.morningbuddies.auth.member.repository.MemberGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.file.service.FileCommandService;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.group.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.domain.notification.service.NotificationCommandService;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupCommandServiceImpl implements GroupCommandService {

	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;
	private final FileCommandService fileCommandService;
	private final GroupJoinRequestRepository groupJoinRequestRepository;

	private final NotificationCommandService notificationCommandService;
	private final MemberGroupRepository memberGroupRepository;

	// 그룹 생성
	@Override
	public GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.CreateGroupDto requestDto, MultipartFile file) {

		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member leader = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

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
			.maxParticipantCount(
				requestDto.getMaxParticipantCount() != null ? requestDto.getMaxParticipantCount() : 0) // Null 체크
			.description(savedGroup.getDescription())
			.imageUrl(savedGroup.getGroupImage())
			.members(members.stream().map(MemberResponseDto.MemberSummaryDTO::from).collect(Collectors.toList()))
			.leader(GroupResponseDto.LeaderDTO.from(savedGroup.getLeader()))
			.build();

	}

	// 그룹 수정
	@Override
	public GroupResponseDto.GroupDetailDTO updateGroup(Long groupId, GroupRequestDto.UpdateGroupDTO request,
		MultipartFile file) {

		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member member = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		if (!group.getLeader().equals(member)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		String uploadedGroupImageUrl = group.getGroupImage();
		if (file != null && !file.isEmpty()) {
			try {
				uploadedGroupImageUrl = fileCommandService.saveFile(file);
			} catch (Exception e) {
				throw new GroupException(GlobalErrorCode.FILE_UPLOAD_FAILED);
			}
		}

		group.setGroupName(request.getGroupName());
		group.setWakeupTime(request.getWakeUpTime());
		group.setMaxParticipantCount(request.getMaxParticipantCount());
		group.setDescription(request.getDescription());
		group.setGroupImage(uploadedGroupImageUrl);

		Groups savedGroup = groupRepository.save(group);

		return GroupResponseDto.GroupDetailDTO.builder()
			.groupId(savedGroup.getId())
			.groupName(savedGroup.getGroupName())
			.description(savedGroup.getDescription())
			.wakeUpTime(savedGroup.getWakeupTime())
			.currentParticipantCount(savedGroup.getCurrentParticipantCount())
			.maxParticipantCount(savedGroup.getMaxParticipantCount())
			.imageUrl(uploadedGroupImageUrl)
			.members(savedGroup.getMembers())
			.leader(GroupResponseDto.LeaderDTO.from(savedGroup.getLeader()))
			.build();
	}

	@Override
	public void deleteGroup(Long groupId) {
		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member member = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		if (!group.getLeader().equals(member)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		groupRepository.delete(group);
	}

	// 그룹 가입 요청
	@Override
	public void requestJoinGroup(Long groupId) {
		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member member = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		GroupJoinRequest joinRequest = GroupJoinRequest.builder()
			.member(member)
			.group(group)
			.status(RequestStatus.PENDING)
			.build();

		groupJoinRequestRepository.save(joinRequest);

		// 그룹 가입 요청 시, 그룹 리더에게 푸시 알림 전송
		// notificationCommandService.sendJoinRequestNotification(group.getLeader(), member, group);

	}

	// 그룹 가입 요청 수락 및 그룹 가입
	@Override
	public void acceptJoinGroup(Long groupId, Long requestId) {

		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member leader = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		GroupJoinRequest joinRequest = groupJoinRequestRepository.findById(requestId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.REQUEST_NOT_FOUND));

		Groups group = joinRequest.getGroup();
		Member member = joinRequest.getMember();

		if (!group.getId().equals(groupId)) {
			throw new GroupException(GlobalErrorCode.GROUP_NOT_FOUND);
		}

		if (!group.getLeader().equals(leader)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		if (group.getCurrentParticipantCount() > group.getMaxParticipantCount()) {
			throw new GroupException(GlobalErrorCode.GROUP_FULL);
		}

		joinRequest.setStatus(RequestStatus.ACCEPTED);
		groupJoinRequestRepository.save(joinRequest);

		addMemberToGroup(group, member);
	}

	private void addMemberToGroup(Groups group, Member member) {
		group.addMember(member);
		group.setCurrentParticipantCount(group.getCurrentParticipantCount() + 1);

		groupRepository.save(group);
	}

	// 그룹 가입 요청 거절
	@Override
	public void rejectJoinGroup(Long groupId, Long requestId) {
		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member leader = memberRepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		GroupJoinRequest joinRequest = groupJoinRequestRepository.findById(requestId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.REQUEST_NOT_FOUND));

		Groups group = joinRequest.getGroup();

		if (!group.getId().equals(groupId)) {
			throw new GroupException(GlobalErrorCode.GROUP_NOT_FOUND);
		}

		if (!group.getLeader().equals(leader)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		joinRequest.setStatus(RequestStatus.REJECTED);

		groupJoinRequestRepository.save(joinRequest);

	}

	// 리더 변경
	@Override
	public void changeLeaderAuthority(Long groupId, Long newLeaderId){
		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member currentLeader = memberRepository.findByEmail(currentEmail)
				.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		if (!group.getLeader().equals(currentLeader)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		Member newLeader = memberRepository.findById(newLeaderId)
				.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		if (!group.getMemberEntities().contains(newLeader)) {
			throw new GroupException(GlobalErrorCode.MEMBER_NOT_IN_GROUP);
		}

		group.setLeader(newLeader);

		groupRepository.save(group);

	}

}
