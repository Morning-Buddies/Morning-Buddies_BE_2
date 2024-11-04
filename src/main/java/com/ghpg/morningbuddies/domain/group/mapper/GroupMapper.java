package com.ghpg.morningbuddies.domain.group.mapper;

import java.util.List;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupMapper {

	/**
	 * 회원이 가입한 그룹의 성공 게임 횟수를 가져온다.
	 * @param member
	 * @return
	 */
	public static Integer getCountSuccessGame(Member member) {
		return member.getGroups().stream()
			.mapToInt(Groups::getSuccessCount)
			.sum();
	}

	public static Groups toNewGroup(GroupRequestDto.CreateGroupDto requestDto, Member leader,
		String uploadedGroupImageUrl) {
		return Groups.builder()
			.groupName(requestDto.getGroupName())
			.description(requestDto.getDescription())
			.wakeupTime(requestDto.getWakeUpTime())
			.successCount(0)
			.groupImageUrl(uploadedGroupImageUrl)
			.currentParticipantCount(1)
			.leader(leader)
			.maxParticipantCount(requestDto.getMaxParticipantCount())
			.build();
	}

	public static GroupResponseDto.GroupDetailDTO toGroupDetailDto(Groups savedGroup,
		String uploadedGroupImageUrl) {
		MemberResponseDto.MemberListResponseDTO members = MemberResponseDto.MemberListResponseDTO.builder()
			.totalCount(savedGroup.getMembers().size())
			.members(savedGroup.getMembers().stream()
				.map(member -> MemberResponseDto.MemberSummaryDTO.builder()
					.id(member.getId())
					.firstName(member.getFirstName())
					.lastName(member.getLastName())
					.build())
				.toList())
			.build();

		return GroupResponseDto.GroupDetailDTO.builder()
			.groupId(savedGroup.getId())
			.groupName(savedGroup.getGroupName())
			.description(savedGroup.getDescription())
			.wakeUpTime(savedGroup.getWakeupTime())
			.currentParticipantCount(savedGroup.getCurrentParticipantCount())
			.maxParticipantCount(savedGroup.getMaxParticipantCount())
			.imageUrl(uploadedGroupImageUrl)
			.members(members)
			.leader(GroupResponseDto.LeaderDTO.from(savedGroup.getLeader()))
			.build();
	}

	/**
	 * 그룹 정보를 GroupInfo로 변환한다.
	 * @param group
	 * @return
	 */
	public static GroupResponseDto.GroupInfo toGroupInfo(Groups group) {
		return GroupResponseDto.GroupInfo.builder()
			.name(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.build();
	}

	/**
	 * 그룹 정보를 GroupInfo로 변환한다.
	 */
	public static GroupResponseDto.GroupListResponseDTO toGroupListResponseDTO(
		List<Groups> groups) {
		return GroupResponseDto.GroupListResponseDTO.builder()
			.totalCount(groups.size())
			.groups(groups.stream()
				.map(group -> GroupResponseDto.GroupInfo.builder()
					.name(group.getGroupName())
					.wakeupTime(group.getWakeupTime())
					.build())
				.toList())
			.build();
	}

}
