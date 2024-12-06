package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponseDto {

	@Builder
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "회원 상세 정보")
	public static class MemberInfo {

		@Schema(description = "회원 ID", example = "1")
		Long id;

		@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
		String profileImage;

		@Schema(description = "이름", example = "동규")
		String firstName;

		@Schema(description = "성", example = "박")
		String lastName;

		@Schema(description = "선호하는 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		LocalTime preferredWakeupTime;

		@Schema(description = "성공한 게임 횟수", example = "5")
		Integer successGameCount;

		@Schema(description = "속한 그룹 목록")
		GroupResponseDto.GroupsResponseDto participatedMemberGroups;

		public static MemberInfo from(Member member) {

			return MemberInfo.builder()
				.id(member.getId())
				.profileImage(member.getProfileImageUrl())
				.firstName(member.getFirstName())
				.lastName(member.getLastName())
				.preferredWakeupTime(member.getPreferredWakeupTime())
				.successGameCount(member.getSuccessGameCount())
				.participatedMemberGroups(GroupResponseDto.GroupsResponseDto.of(member.getMemberGroups()))
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED) // access level 수정
	@AllArgsConstructor
	@Schema(description = "회원 요약 정보")
	public static class MemberSummaryDTO {
		@Schema(description = "회원 ID", example = "1")
		Long id;

		@Schema(description = "이름", example = "동규")
		String firstName;

		@Schema(description = "성", example = "박")
		String lastName;

		@Schema(description = "이메일", example = "test@example.com")
		String email;

		@Schema(description = "리더 여부", example = "true")
		boolean isLeader;

		public static MemberSummaryDTO of(Member member, boolean isLeader) {
			return MemberSummaryDTO.builder()
				.id(member.getId())
				.firstName(member.getFirstName())
				.lastName(member.getLastName())
				.email(member.getEmail())
				.isLeader(isLeader)
				.build();
		}

	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "회원 요약 정보 리스트")
	public static class MemberListResponseDTO {
		@Schema(description = "전체 회원 수", example = "5")
		int totalCount;

		@Schema(description = "회원 목록")
		List<MemberSummaryDTO> members;

		public static MemberListResponseDTO of(List<MemberGroup> memberGroups) {
			return MemberListResponseDTO.builder()
				.totalCount(memberGroups.size())
				.members(memberGroups.stream()
					.map(mg -> MemberSummaryDTO.of(mg.getMember(), mg.isLeader()))
					.toList())
				.build();
		}
	}
}