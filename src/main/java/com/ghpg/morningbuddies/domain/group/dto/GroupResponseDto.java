package com.ghpg.morningbuddies.domain.group.dto;

import java.time.LocalTime;
import java.util.List;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;

import com.ghpg.morningbuddies.domain.group.entity.enums.RequestStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupResponseDto {

	// 그룹 정보 DTO
	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupDetailDTO {
		private Long groupId;
		private String groupName;
		private String description;
		private LocalTime wakeUpTime;
		private int currentParticipantCount;
		private int maxParticipantCount;
		private String imageUrl;
		private List<MemberResponseDto.MemberSummaryDTO> members;
		private LeaderDTO leader;
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

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupInfo {
		private Long id;
		private String name;
		private LocalTime wakeupTime;
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
		private byte[] image;
		private String name;
		private String description;
		private LocalTime wakeupTime;
		private Integer currentParticipantCount;
		private Integer maxParticipantCount;
	}
}
