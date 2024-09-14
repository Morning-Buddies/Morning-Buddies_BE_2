package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;
import java.util.List;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class MemberInfo {
		private Long id;

		private byte[] profileImage;

		private String firstName;

		private String lastName;

		private LocalTime preferredWakeupTime;

		private Integer successGameCount;

		private List<GroupResponseDto.GroupInfo> groups;

		private LocalTime wakeupTime;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MemberSummaryDTO {
		private Long id;
		private String firstName;
		private String lastName;
		private String email;

		public static MemberSummaryDTO from(Member member) {
			return new MemberSummaryDTO(
				member.getId(),
				member.getFirstName(),
				member.getLastName(),
				member.getEmail()
			);
		}
	}
}
