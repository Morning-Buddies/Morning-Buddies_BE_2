package com.ghpg.morningbuddies.domain.groups.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDTO;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.group.GroupException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupResponseDTO {

	// 그룹 정보 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupDetailDTO {

		@Schema(description = "그룹 ID", example = "1")
		Long groupId;

		@Schema(description = "그룹 이름", example = "아침형 인간 모임")
		String groupName;

		@Schema(description = "그룹 설명", example = "아침형 인간 모임입니다.")
		String description;

		@Schema(description = "그룹 선호 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		LocalTime wakeUpTime;

		@Schema(description = "그룹 최대 인원 수", example = "5")
		int currentParticipantCount;

		@Schema(description = "그룹 최대 인원 수", example = "5")
		int maxParticipantCount;

		@Schema(description = "그룹 이미지 URL", example = "https://example.com/group.jpg")
		String imageUrl;

		@Schema(description = "그룹 멤버 목록")
		MemberResponseDTO.MembersResponseDTO members;

		public static GroupDetailDTO from(Groups group) {

			return GroupDetailDTO.builder()
				.groupId(group.getId())
				.groupName(group.getGroupName())
				.description(group.getDescription())
				.wakeUpTime(group.getWakeupTime())
				.currentParticipantCount(group.getCurrentParticipantCount())
				.maxParticipantCount(group.getMaxParticipantCount())
				.imageUrl(group.getGroupImageUrl())
				.members(MemberResponseDTO.MembersResponseDTO.of(group.getMemberGroups()))
				.build();
		}

		public static GroupDetailDTO of(List<MemberGroup> memberGroups) {
			return memberGroups.stream()
				.map(mg -> GroupDetailDTO.builder()
					.groupId(mg.getGroup().getId())
					.groupName(mg.getGroup().getGroupName())
					.description(mg.getGroup().getDescription())
					.wakeUpTime(mg.getGroup().getWakeupTime())
					.currentParticipantCount(mg.getGroup().getCurrentParticipantCount())
					.maxParticipantCount(mg.getGroup().getMaxParticipantCount())
					.imageUrl(mg.getGroup().getGroupImageUrl())
					.members(MemberResponseDTO.MembersResponseDTO.of(mg.getGroup().getMemberGroups()))
					.build())
				.findFirst()
				.orElseThrow(() -> new GroupException(ErrorStatus.GROUP_NOT_FOUND));
		}

	}

	// 리더 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class LeaderDTO {
		Long id;
		String firstName;
		String lastName;
		String email;

		public static LeaderDTO from(Member member) {
			return LeaderDTO.builder()
				.id(member.getId())
				.firstName(member.getFirstName())
				.lastName(member.getLastName())
				.email(member.getEmail())
				.build();
		}
	}

	// 내가 속한 그룹 리스트 응답 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupsResponseDto {

		@Schema(description = "전체 그룹 수", example = "3")
		int totalCount;

		@Schema(description = "내가 속한 그룹 목록")
		Set<GroupInfo> groups;

		public static GroupsResponseDto of(Set<MemberGroup> memberGroups) {
			return GroupsResponseDto.builder()
				.totalCount(memberGroups.size())
				.groups(GroupInfo.of(memberGroups))
				.build();
		}

	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupInfo {

		@Schema(description = "그룹 ID", example = "1")
		Long id;

		@Schema(description = "그룹 이름", example = "아침형 인간 모임")
		String name;

		@Schema(description = "그룹 선호 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		LocalTime wakeupTime;

		@Schema(description = "리더 여부", example = "true")
		Boolean isLeader;

		public static GroupInfo from(MemberGroup memberGroup) {
			Groups currentGroup = memberGroup.getGroup();

			return GroupInfo.builder()
				.id(currentGroup.getId())
				.name(currentGroup.getGroupName())
				.wakeupTime(currentGroup.getWakeupTime())
				.isLeader(memberGroup.getIsLeader())
				.build();
		}

		public static Set<GroupInfo> of(Set<MemberGroup> memberGroups) {
			return memberGroups.stream()
				.map(GroupInfo::from)
				.collect(Collectors.toSet());
		}

	}

	// 그룹 요청 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class JoinRequestDTO {
		Long requestId;
		Long memberId;
		String firstName;
		String lastName;
		String email;
		RequestStatus status;
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class SearchedGroupInfoList {
		List<SearchedGroupInfo> searchedGroupInfoList;
		Integer listSize;
		Integer totalPage;
		Long totalElements;
		Boolean isFirst;
		Boolean isLast;
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class SearchedGroupInfo {
		Long id;
		String imageUrl;
		String name;
		String description;
		LocalTime wakeupTime;
		Integer currentParticipantCount;
		Integer maxParticipantCount;
	}

	// 그룹 요약 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupSummaryDTO {
		Long id;
		String groupName;
		LocalTime wakeupTime;
		Integer currentParticipantCount;
		Integer maxParticipantCount;
		String groupImage;
	}
}
