package com.ghpg.morningbuddies.domain.group.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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

}
