package com.ghpg.morningbuddies.domain.groups.service.command;

import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomJpaRepository;
import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;
import com.ghpg.morningbuddies.domain.groups.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDTO;
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
	private final ChatRoomJpaRepository chatRoomJpaRepository;
	private final MemberChatRoomJPARepository memberChatRoomJPARepository;
	private final MemberGroupJPARepository memberGroupJPARepository;

	@Override
	public GroupResponseDTO.GroupDetailDTO createGroup(GroupRequestDto.GroupCommand requestDto, MultipartFile file) {

		// 현재 로그인한 사용자 정보 가져오기
		Member currentMember = getCurrentMember();

		// 이미지를 s3를 사용해서 업로드 후 반환.
		String uploadedGroupImageUrl = getUploadedGroupImageUrl(file);

		// 그룹 생성
		Groups newGroup = Groups.createGroup(requestDto.getGroupName(),
			requestDto.getDescription(),
			requestDto.getWakeUpTime(),
			requestDto.getMaxParticipantCount());

		// 그룹장으로 멤버그룹 생성
		MemberGroup newMemberGroup = MemberGroup.createMemberGroup(currentMember, newGroup, true);

		// 그룹 저장하면 cascade에 의해 newMemberGroup 저장.
		groupJPARepository.save(newGroup);

		chatRoomCommandService.createNewChatRoom(newGroup, currentMember);

		// 새롭게 만들어진 그룹의 정보를 리턴
		return GroupResponseDTO.GroupDetailDTO.from(newGroup);

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
	 * @return GroupResponseDTO.GroupDetailDTO
	 */
	@Override
	public GroupResponseDTO.GroupDetailDTO updateGroup(Long groupId, GroupRequestDto.GroupCommand request,
		MultipartFile file) {

		// 그룹 정보 가져오기
		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));
		// 현재 로그인한 사용자 정보 가져오기
		Member currentMember = getCurrentMember();

		// 그룹장만 그룹 정보 수정 가능
		if (!group.isLeader(currentMember)) {
			throw new GroupException(ErrorStatus.GROUP_PERMISSION_DENIED);
		}

		String uploadedGroupImageUrl = getUploadedGroupImageUrl(file);

		group.updateGroup(request, uploadedGroupImageUrl);

		return GroupResponseDTO.GroupDetailDTO.from(group);

	}

	/**
	 * 그룹 탈퇴
	 *
	 * @param groupId
	 */
	@Override
	public Void leaveGroup(Long groupId) {
		Member currentMember = getCurrentMember();

		Groups currentGroup = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));

		currentGroup.leave(currentMember);

		return null;

	}

	@Override
	public Void deleteGroup(Long groupId) {

		// 현재 멤버 가져오기
		Member currentMember = getCurrentMember();

		// 그룹 가져오기
		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));

		// 그룹장만 그룹 삭제 가능
		if (!group.isLeader(currentMember)) {
			throw new GroupException(ErrorStatus.GROUP_PERMISSION_DENIED);
		}

		groupJPARepository.delete(group);

		return null;
	}

	/**
	 * 그룹 가입 요청
	 *
	 * @param groupId
	 */
	@Override
	public void requestJoinGroup(Long groupId) {
		Member currentMember = getCurrentMember();

		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));

		GroupJoinRequest joinRequest = GroupJoinRequest.builder()
			.member(currentMember)
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

		Member leader = getCurrentMember();

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

	/**
	 * 그룹 가입 요청 거절
	 *
	 * @param groupId
	 * @param requestId
	 */
	@Override
	public void rejectJoinGroup(Long groupId, Long requestId) {

		Member leader = getCurrentMember();

		GroupJoinRequest joinRequest = groupJoinRequestRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.REQUEST_NOT_FOUND));

		Groups group = joinRequest.getGroup();

		validateJoinRequest(group, groupId, leader);

		joinRequest.reject();

		groupJoinRequestRepository.save(joinRequest);

		// 가입 요청이 거절되었다는 알림 전송
		// notificationCommandService.sendJoinRequestRejectedNotification(leader, group);

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
	 * 그룹의 반장 권한 변경
	 *
	 * @param groupId, newLeaderId
	 */
	@Override
	public void changeLeaderAuthority(Long groupId, Long newLeaderId) {

		Member currentLeader = getCurrentMember();

		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));

		Member newLeader = memberJPARepository.findById(newLeaderId)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		group.transferLeadershop(currentLeader, newLeader);

		groupJPARepository.save(group);

	}

}
