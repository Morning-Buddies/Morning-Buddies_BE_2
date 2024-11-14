package com.ghpg.morningbuddies.domain.groups.dto;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.entity.enums.RequestStatus;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupResponseDto {

	// 그룹 정보 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupDetailDTO {

		@Schema(description = "그룹 ID", example = "1")
		private Long groupId;

		@Schema(description = "그룹 이름", example = "아침형 인간 모임")
		private String groupName;

		@Schema(description = "그룹 설명", example = "아침형 인간 모임입니다.")
		private String description;

		@Schema(description = "그룹 선호 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime wakeUpTime;

		@Schema(description = "그룹 최대 인원 수", example = "5")
		private int currentParticipantCount;

		@Schema(description = "그룹 최대 인원 수", example = "5")
		private int maxParticipantCount;

		@Schema(description = "그룹 이미지 URL", example = "https://example.com/group.jpg")
		private String imageUrl;

		@Schema(description = "그룹 멤버 목록")
		private MemberResponseDto.MemberListResponseDTO members;

		@Schema(description = "그룹 리더 정보")
		private LeaderDTO leader;

		public static GroupDetailDTO of(Groups group) {
			return GroupDetailDTO.builder()
				.groupId(group.getId())
				.groupName(group.getGroupName())
				.description(group.getDescription())
				.wakeUpTime(group.getWakeupTime())
				.currentParticipantCount(group.getCurrentParticipantCount())
				.maxParticipantCount(group.getMaxParticipantCount())
				.imageUrl(group.getGroupImageUrl())
				.members(MemberResponseDto.MemberListResponseDTO.of(
					group.getMemberGroups().stream().map(MemberGroup::getMember).toList()))
				.leader(LeaderDTO.from(group.getLeader()))
				.build();
		}
	}

	// 리더 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class LeaderDTO {
		private Long id;
		private String firstName;
		private String lastName;
		private String email;

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
	public static class GroupListResponseDTO {
		private int totalCount;

		@Schema(description = "내가 속한 그룹 목록")
		private List<GroupResponseDto.GroupInfo> groups;

		public static GroupListResponseDTO of(List<Groups> groups) {
			return GroupListResponseDTO.builder()
				.totalCount(groups.size())
				.groups(GroupResponseDto.GroupInfo.of(groups))
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupInfo {
		private Long id;
		private String name;

		@Schema(description = "그룹 선호 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime wakeupTime;

		public static List<GroupInfo> of(List<Groups> groups) {
			return groups.stream()
				.map(group -> GroupInfo.builder()
					.id(group.getId())
					.name(group.getGroupName())
					.wakeupTime(group.getWakeupTime())
					.build())
				.toList();
		}
	}

	// 그룹 요청 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class JoinRequestDTO {
		private Long requestId;
		private Long memberId;
		private String firstName;
		private String lastName;
		private String email;
		private RequestStatus status;
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
		private Long id;
		private String imageUrl;
		private String name;
		private String description;
		private LocalTime wakeupTime;
		private Integer currentParticipantCount;
		private Integer maxParticipantCount;
	}

	// 그룹 요약 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupSummaryDTO {
		private Long id;
		private String groupName;
		private LocalTime wakeupTime;
		private Integer currentParticipantCount;
		private Integer maxParticipantCount;
		private String groupImage;
	}
}
