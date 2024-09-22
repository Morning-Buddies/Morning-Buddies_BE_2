package com.ghpg.morningbuddies.domain.group.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ghpg.morningbuddies.auth.member.entity.MemberChatRoom;
import com.ghpg.morningbuddies.auth.member.repository.MemberChatRoomRepository;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomRepository;
import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberGroupRepository;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.group.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.domain.notification.service.NotificationCommandService;
import com.ghpg.morningbuddies.global.aws.s3.S3Service;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupCommandServiceImpl implements GroupCommandService {

	private final MemberRepository memberRepository;
	private final GroupRepository groupRepository;
	private final GroupJoinRequestRepository groupJoinRequestRepository;
	private final NotificationCommandService notificationCommandService;
	private final ChatRoomCommandService chatRoomCommandService;

	private final S3Service s3Service;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;

	/**
	 * 그룹 생성
	 * @param requestDto
	 * @param file
	 * @return GroupResponseDto.GroupDetailDTO
	 */
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
			uploadedGroupImageUrl = s3Service.uploadImage(file);

		}

		Groups group = Groups.builder()
			.groupName(requestDto.getGroupName())
			.description(requestDto.getDescription())
			.wakeupTime(requestDto.getWakeUpTime())
			.currentParticipantCount(1)
			.leader(leader)
			.maxParticipantCount(requestDto.getMaxParticipantCount())
			.isActivated(true)
			.groupImageUrl(uploadedGroupImageUrl)
			.build();

		group.addMember(leader);

		Groups savedGroup = groupRepository.save(group);

		ArrayList<Member> members = new ArrayList<>();
		members.add(leader);

		// 채팅방 생성
		ChatRoomRequestDto chatRoomDto = chatRoomCommandService.createChatRoom(savedGroup.getId(), leader);

		// MemberChatRoom 엔터티 생성
		MemberChatRoom memberChatRoom = MemberChatRoom.builder()
				.member(leader)
				.chatRoom(chatRoomRepository.findById(chatRoomDto.getChatRoomId())
						.orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다")))
				.build();

		memberChatRoomRepository.save(memberChatRoom);

		return GroupResponseDto.GroupDetailDTO.builder()
			.groupId(savedGroup.getId())
			.groupName(savedGroup.getGroupName())
			.wakeUpTime(savedGroup.getWakeupTime())
			.currentParticipantCount(savedGroup.getCurrentParticipantCount())
			.maxParticipantCount(
				requestDto.getMaxParticipantCount() != null ? requestDto.getMaxParticipantCount() : 0) // Null 체크
			.description(savedGroup.getDescription())
			.imageUrl(savedGroup.getGroupImageUrl())
			.members(members.stream().map(MemberResponseDto.MemberSummaryDTO::from).collect(Collectors.toList()))
			.leader(GroupResponseDto.LeaderDTO.from(savedGroup.getLeader()))
			.build();

	}

	/**
	 * 그룹 정보 수정
	 * @param groupId
	 * @param request
	 * @param file
	 * @return GroupResponseDto.GroupDetailDTO
	 */
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

		String uploadedGroupImageUrl = group.getGroupImageUrl();
		if (file != null && !file.isEmpty()) {
			uploadedGroupImageUrl = s3Service.uploadImage(file);
		}

		group.setGroupName(request.getGroupName());
		group.setWakeupTime(request.getWakeUpTime());
		group.setMaxParticipantCount(request.getMaxParticipantCount());
		group.setDescription(request.getDescription());
		group.setGroupImageUrl(uploadedGroupImageUrl);

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

	/**
	 * 그룹 삭제
	 * @param groupId
	 */
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

	/**
	 * 그룹 가입 요청
	 * @param groupId
	 */
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

	/**
	 * 그룹 가입 요청 수락 및 그룹 가입
	 * @param groupId
	 * @param requestId
	 */
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

		// 가입 요청이 수락되었다는 알림 전송
		notificationCommandService.sendJoinRequestAcceptedNotification(member, group);
	}

	/**
	 * 그룹에 멤버 추가
	 * @param group
	 * @param member
	 */
	private void addMemberToGroup(Groups group, Member member) {
		group.addMember(member);
		group.setCurrentParticipantCount(group.getCurrentParticipantCount() + 1);

		groupRepository.save(group);
	}

	/**
	 * 그룹 가입 요청 거절
	 * @param groupId
	 * @param requestId
	 */
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

		// 가입 요청이 거절되었다는 알림 전송
		notificationCommandService.sendJoinRequestRejectedNotification(leader, group);

	}

	/**
	 * 그룹 탈퇴
	 * @param groupId
	 */
	@Override
	public void changeLeaderAuthority(Long groupId, Long newLeaderId) {
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
