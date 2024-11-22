package com.ghpg.morningbuddies.domain.groups.service.command;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomRepository;
import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;
import com.ghpg.morningbuddies.domain.groups.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.groups.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.domain.groups.repository.GroupRepository;
import com.ghpg.morningbuddies.domain.memberchatroom.repository.MemberChatRoomJPARepository;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.membergroup.repository.MemberGroupJPARepository;
import com.ghpg.morningbuddies.domain.notification.service.NotificationCommandService;
import com.ghpg.morningbuddies.global.aws.s3.S3Service;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
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

	private final MemberJPARepository memberJPARepository;
	private final GroupJPARepository groupJPARepository;
	private final GroupJoinRequestRepository groupJoinRequestRepository;
	private final NotificationCommandService notificationCommandService;
	private final ChatRoomCommandService chatRoomCommandService;
	private final GroupRepository groupRepository;

	private final S3Service s3Service;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomJPARepository memberChatRoomJPARepository;
	private final MemberGroupJPARepository memberGroupJPARepository;

	@Override
	public GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.GroupCommand requestDto, MultipartFile file) {

		// 현재 로그인한 사용자 정보 가져오기
		Member currentMember = getCurrentMember();

		String uploadedGroupImageUrl = getUploadedGroupImageUrl(file);

		// 리더의 MemberGroup 생성
		MemberGroup leaderMemberGroup = MemberGroup.createMemberGroup(currentMember);

		// 새로운 그룹 생성 및 리더의 MemberGroup과 연결
		Groups newGroup = Groups.createGroup(requestDto, uploadedGroupImageUrl, currentMember, leaderMemberGroup);

		// 그룹 저장
		groupJPARepository.save(newGroup);

		// 그룹과 연동된 채팅방 생성
		chatRoomCommandService.createNewChatRoom(newGroup, currentMember);

		return GroupResponseDto.GroupDetailDTO.of(newGroup);

	}

	/**
	 * 현재 로그인한 사용자 정보 가져오기
	 * @return Member
	 */
	private Member getCurrentMember() {
		return memberJPARepository.findByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));
	}

	/**
	 * 이미지 업로드
	 * @param file
	 * @return String
	 */
	private @Nullable String getUploadedGroupImageUrl(MultipartFile file) {
		// 이미지 업로드
		String uploadedGroupImageUrl = null;
		if (file != null && !file.isEmpty()) {
			uploadedGroupImageUrl = s3Service.uploadImage(file);

		}
		return uploadedGroupImageUrl;
	}

	/**
	 * 그룹 정보 수정
	 * @param groupId
	 * @param request
	 * @param file
	 * @return GroupResponseDto.GroupDetailDTO
	 */
	@Override
	public GroupResponseDto.GroupDetailDTO updateGroup(Long groupId, GroupRequestDto.GroupCommand request,
		MultipartFile file) {

		Groups group = getGroupAllInfoByGroupId(groupId);

		// 현재 로그인한 사용자 정보 가져오기
		Member currentMember = getCurrentMember();

		if (!group.getLeader().equals(currentMember)) {
			throw new GroupException(ErrorStatus.GROUP_PERMISSION_DENIED);
		}

		String uploadedGroupImageUrl = getUploadedGroupImageUrl(file);

		group.updateGroup(request, uploadedGroupImageUrl);

		return GroupResponseDto.GroupDetailDTO.of(group);

	}

	/**
	 * 그룹 탈퇴
	 *
	 * @param groupId
	 */
	@Override
	public Void leaveGroup(Long groupId) {
		Member currentMember = getCurrentMember();

		Groups currentGroup = getGroupAllInfoByGroupId(groupId);

		currentGroup.leave(currentMember);

		return null;

	}

	@Override
	public Void deleteGroup(Long groupId) {

		// 현재 멤버 가져오기
		Member currentMember = getCurrentMember();

		// 그룹 가져오기
		Groups group = getGroupAllInfoByGroupId(groupId);

		if (!group.getLeader().equals(currentMember)) {
			throw new GroupException(ErrorStatus.GROUP_PERMISSION_DENIED);
		}

		groupJPARepository.delete(group);

		return null;
	}

	private Groups getGroupAllInfoByGroupId(Long groupId) {
		return groupRepository.findGroupAndMemberGroupsAndGroupByGroupId(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));
	}

	/**
	 * 그룹 가입 요청
	 *
	 * @param groupId
	 */
	@Override
	public void requestJoinGroup(Long groupId) {
		String currentEmail = SecurityUtil.getCurrentUserEmail();
		Member member = memberJPARepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		Groups group = getGroupAllInfoByGroupId(groupId);

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
	 *
	 * @param groupId
	 * @param requestId
	 */
	@Override
	public void acceptJoinGroup(Long groupId, Long requestId) {

		String currentEmail = SecurityUtil.getCurrentUserEmail();
		Member leader = memberJPARepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		GroupJoinRequest joinRequest = groupJoinRequestRepository.findById(requestId)
			.orElseThrow(() -> new GroupException(ErrorStatus.REQUEST_NOT_FOUND));

		Groups group = joinRequest.getGroup();
		Member member = joinRequest.getMember();

		validateJoinRequest(group, groupId, leader);

		joinRequest.accept();

		group.addMemberGroup(MemberGroup.builder()
			.member(member)
			.build());

		groupJoinRequestRepository.save(joinRequest);
		//notificationCommandService.sendJoinRequestAcceptedNotification(member, group);
	}

	private void validateJoinRequest(Groups group, Long groupId, Member leader) {
		if (!group.getId().equals(groupId)) {
			throw new GroupException(ErrorStatus.GROUP_NOT_FOUND);
		}

		if (!group.isLeader(leader)) {
			throw new GroupException(ErrorStatus.GROUP_PERMISSION_DENIED);
		}

	}

	/**
	 * 그룹 가입 요청 거절
	 *
	 * @param groupId
	 * @param requestId
	 */
	@Override
	public void rejectJoinGroup(Long groupId, Long requestId) {
		// String currentEmail = SecurityUtil.getCurrentUserEmail();
		// Member leader = memberJPARepository.findByEmail(currentEmail)
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// GroupJoinRequest joinRequest = groupJoinRequestRepository.findById(requestId)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.REQUEST_NOT_FOUND));
		//
		// Groups group = joinRequest.getGroupAllInfoByGroupId();
		//
		// if (!group.getId().equals(groupId)) {
		// 	throw new GroupException(GlobalErrorCode.GROUP_NOT_FOUND);
		// }
		//
		// if (!group.getLeader().equals(leader)) {
		// 	throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		// }
		//
		// joinRequest.setStatus(RequestStatus.REJECTED);
		//
		// groupJoinRequestRepository.save(joinRequest);
		//
		// // 가입 요청이 거절되었다는 알림 전송
		// notificationCommandService.sendJoinRequestRejectedNotification(leader, group);

	}

	/**
	 * 그룹 탈퇴
	 *
	 * @param groupId
	 */
	@Override
	public void changeLeaderAuthority(Long groupId, Long newLeaderId) {
		// String currentEmail = SecurityUtil.getCurrentUserEmail();
		// Member currentLeader = memberRepository.findByEmail(currentEmail)
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// Groups group = groupRepository.findById(groupId)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));
		//
		// if (!group.getLeader().equals(currentLeader)) {
		// 	throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		// }
		//
		// Member newLeader = memberRepository.findById(newLeaderId)
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// if (!group.getMemberEntities().contains(newLeader)) {
		// 	throw new GroupException(GlobalErrorCode.MEMBER_NOT_IN_GROUP);
		// }
		//
		// group.setLeader(newLeader);
		//
		// groupRepository.save(group);

	}

}
