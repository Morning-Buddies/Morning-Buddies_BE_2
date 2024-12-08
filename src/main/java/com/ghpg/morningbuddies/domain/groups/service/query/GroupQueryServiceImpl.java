package com.ghpg.morningbuddies.domain.groups.service.query;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.groups.converter.GroupConverter;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDTO;
import com.ghpg.morningbuddies.domain.groups.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.membergroup.repository.MemberGroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryServiceImpl implements GroupQueryService {

	private final GroupJPARepository groupJPARepository;
	private final MemberGroupRepository memberGroupRepository;
	private final MemberJPARepository memberJPARepository;
	private final GroupJoinRequestRepository groupJoinRequestRepository;

	// 그룹 정보 가져오기
	@Override
	public GroupResponseDTO.GroupDetailDTO getGroupDetailById(Long groupId) {
		List<MemberGroup> memberGroups = memberGroupRepository.findMemberGroupsByGroupId(groupId);

		return GroupResponseDTO.GroupDetailDTO.of(memberGroups);
	}

	@Override
	public GroupResponseDTO.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable) {
		Page<Groups> groupsPage = groupJPARepository
			.findByGroupNameOrDescriptionContaining(keyword, null);

		List<GroupResponseDTO.SearchedGroupInfo> searchedGroupInfoList = groupsPage.getContent().stream()
			.map(GroupConverter::convertToSearchedGroupInfo)
			.collect(Collectors.toList());

		return GroupResponseDTO.SearchedGroupInfoList.builder()
			.searchedGroupInfoList(searchedGroupInfoList)
			.listSize(searchedGroupInfoList.size())
			.totalPage(groupsPage.getTotalPages())
			.totalElements(groupsPage.getTotalElements())
			.isFirst(groupsPage.isFirst())
			.isLast(groupsPage.isLast())
			.build();
	}

	// 그룹 가입 요청 리스트
	@Override
	public List<GroupResponseDTO.JoinRequestDTO> findByGroupAndStatus(Long groupId) {
		String currentEmail = SecurityUtil.getCurrentUserEmail();
		Member member = memberJPARepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));

		List<GroupJoinRequest> joinRequests = groupJoinRequestRepository.findByGroupAndStatus(group,
			RequestStatus.PENDING);

		return joinRequests.stream()
			.map(request -> GroupResponseDTO.JoinRequestDTO.builder()
				.requestId(request.getId())
				.memberId(request.getMember().getId())
				.firstName(request.getMember().getFirstName())
				.lastName(request.getMember().getLastName())
				.email(request.getMember().getEmail())
				.status(request.getStatus())
				.build())
			.collect(Collectors.toList());

	}

	// 생성된 모든 그룹 리스트 가져오기
	@Override
	public Page<GroupResponseDTO.GroupSummaryDTO> getAllGroups(Integer page, Integer size) {
		Page<Groups> groups = groupJPARepository.findAll(PageRequest.of(page, size));

		return groups.map(group -> GroupResponseDTO.GroupSummaryDTO.builder()
			.id(group.getId())
			.groupName(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.groupImage(group.getGroupImageUrl())
			.build());
	}

	// 핫한 그룹 기준
	@Override
	public Page<GroupResponseDTO.GroupSummaryDTO> getHotGroups(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Groups> hotGroups = groupJPARepository.getHotGroups(pageRequest);

		return hotGroups.map(group -> GroupResponseDTO.GroupSummaryDTO.builder()
			.id(group.getId())
			.groupName(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.groupImage(group.getGroupImageUrl())
			.build());
	}

	// 일찍 일어나는 그룹 기준
	@Override
	public Page<GroupResponseDTO.GroupSummaryDTO> getEarlyMorningGroups(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		LocalTime earlyMorningTime = LocalTime.of(6, 0);
		Page<Groups> earlyMorningGroups = groupJPARepository.getGroupsByEarlyMorning(earlyMorningTime, pageRequest);

		return earlyMorningGroups.map(group -> GroupResponseDTO.GroupSummaryDTO.builder()
			.id(group.getId())
			.groupName(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.groupImage(group.getGroupImageUrl())
			.build());

	}

	// 늦게 일어나는 그룹 기준
	@Override
	public Page<GroupResponseDTO.GroupSummaryDTO> getGroupsByLateEvening(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		LocalTime lateEveningTime = LocalTime.of(18, 0);
		Page<Groups> lateEveningGroups = groupJPARepository.getGroupsByLateEvening(lateEveningTime, pageRequest);

		return lateEveningGroups.map(group -> GroupResponseDTO.GroupSummaryDTO.builder()
			.id(group.getId())
			.groupName(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.groupImage(group.getGroupImageUrl())
			.build());
	}

}
