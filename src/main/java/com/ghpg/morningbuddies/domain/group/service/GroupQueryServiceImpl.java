package com.ghpg.morningbuddies.domain.group.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ghpg.morningbuddies.domain.group.entity.GroupJoinRequest;
import com.ghpg.morningbuddies.domain.group.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.group.repository.GroupJoinRequestRepository;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.group.converter.GroupConverter;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;

import lombok.RequiredArgsConstructor;

import javax.swing.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupQueryServiceImpl implements GroupQueryService {

	private final GroupRepository groupRepository;
	private final MemberRepository memberRepository;
	private final GroupJoinRequestRepository groupJoinRequestRepository;

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

	@Override
	public GroupResponseDto.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable) {
		Page<Groups> groupsPage = groupRepository
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
	public List<GroupResponseDto.JoinRequestDTO> findByGroupAndStatus(Long groupId){
		String currentEmail = SecurityUtil.getCurrentMemberEmail();
		Member member = memberRepository.findByEmail(currentEmail)
				.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		Groups group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		if (!group.getLeader().equals(member)){
			throw new GroupException(GlobalErrorCode.GROUP_PERMISSION_DENIED);
		}

		List<GroupJoinRequest> joinRequests = groupJoinRequestRepository.findByGroupAndStatus(group, RequestStatus.PENDING);

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
	public Page<GroupResponseDto.GroupSummaryDTO> getAllGroups(Integer page, Integer size){
		Page<Groups> groups = groupRepository.findAll(PageRequest.of(page, size));

		return groups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
				.id(group.getId())
				.groupName(group.getGroupName())
				.wakeupTime(group.getWakeupTime())
				.currentParticipantCount(group.getCurrentParticipantCount())
				.maxParticipantCount(group.getMaxParticipantCount())
				.groupImage(group.getGroupImage())
				.build());
	}

	// 핫한 그룹 기준
	@Override
	public Page<GroupResponseDto.GroupSummaryDTO> getHotGroups(Integer page, Integer size){
		PageRequest pageRequest = PageRequest.of(page, size);
		Page<Groups> hotGroups = groupRepository.getHotGroups(pageRequest);

		return hotGroups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
				.id(group.getId())
				.groupName(group.getGroupName())
				.wakeupTime(group.getWakeupTime())
				.currentParticipantCount(group.getCurrentParticipantCount())
				.maxParticipantCount(group.getMaxParticipantCount())
				.groupImage(group.getGroupImage())
				.build());
	}

	// 일찍 일어나는 그룹 기준
	@Override
	public Page<GroupResponseDto.GroupSummaryDTO> getEarlyMorningGroups(Integer page, Integer size){
		PageRequest pageRequest = PageRequest.of(page, size);
		LocalTime earlyMorningTime = LocalTime.of(6, 0);
		Page<Groups> earlyMorningGroups = groupRepository.getGroupsByEarlyMorning(earlyMorningTime, pageRequest);

		return earlyMorningGroups.map(group -> GroupResponseDto.GroupSummaryDTO.builder()
				.id(group.getId())
				.groupName(group.getGroupName())
				.wakeupTime(group.getWakeupTime())
				.currentParticipantCount(group.getCurrentParticipantCount())
				.maxParticipantCount(group.getMaxParticipantCount())
				.groupImage(group.getGroupImage())
				.build());
	}

}
