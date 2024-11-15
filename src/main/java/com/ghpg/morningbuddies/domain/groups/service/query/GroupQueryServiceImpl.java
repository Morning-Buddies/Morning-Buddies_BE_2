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
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.groups.converter.GroupConverter;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.groups.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryServiceImpl implements GroupQueryService {

	private final GroupJPARepository groupJPARepository;
	private final MemberJPARepository memberJPARepository;
	private final GroupJoinRequestRepository groupJoinRequestRepository;
	private final MemberRepository memberRepository;

	// 그룹 정보 가져오기
	@Override
	public GroupResponseDto.GroupDetailDTO getGroupDetailById(Long groupId) {
		return null;
	}

	@Override
	public GroupResponseDto.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable) {
		Page<Groups> groupsPage = groupJPARepository
			.findByGroupNameOrDescriptionContaining(keyword, null);

		List<GroupResponseDto.SearchedGroupInfo> searchedGroupInfoList = groupsPage.getContent().stream()
			.map(GroupConverter::convertToSearchedGroupInfo)
			.collect(Collectors.toList());

		return GroupResponseDto.SearchedGroupInfoList.builder()
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
	public List<GroupResponseDto.JoinRequestDTO> findByGroupAndStatus(Long groupId) {
		String currentEmail = SecurityUtil.getCurrentUserEmail();
		Member member = memberJPARepository.findByEmail(currentEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupJPARepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		if (!group.getLeader().equals(member)) {
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		List<GroupJoinRequest> joinRequests = groupJoinRequestRepository.findByGroupAndStatus(group,
			RequestStatus.PENDING);

		return joinRequests.stream()
			.map(request -> GroupResponseDto.JoinRequestDTO.builder()
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
	public Page<GroupResponseDto.GroupSummaryDTO> getAllGroups(Integer page, Integer size) {
		Page<Groups> groups = groupJPARepository.findAll(PageRequest.of(page, size));

		return groups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
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
	public Page<GroupResponseDto.GroupSummaryDTO> getHotGroups(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Groups> hotGroups = groupJPARepository.getHotGroups(pageRequest);

		return hotGroups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
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
	public Page<GroupResponseDto.GroupSummaryDTO> getEarlyMorningGroups(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		LocalTime earlyMorningTime = LocalTime.of(6, 0);
		Page<Groups> earlyMorningGroups = groupJPARepository.getGroupsByEarlyMorning(earlyMorningTime, pageRequest);

		return earlyMorningGroups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
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
	public Page<GroupResponseDto.GroupSummaryDTO> getGroupsByLateEvening(Integer page, Integer size) {

		PageRequest pageRequest = PageRequest.of(page, size);
		LocalTime lateEveningTime = LocalTime.of(18, 0);
		Page<Groups> lateEveningGroups = groupJPARepository.getGroupsByLateEvening(lateEveningTime, pageRequest);

		return lateEveningGroups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
			.id(group.getId())
			.groupName(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.groupImage(group.getGroupImageUrl())
			.build());
	}

}
