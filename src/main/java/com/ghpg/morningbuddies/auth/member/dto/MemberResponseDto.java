package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponseDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "회원 상세 정보")
	public static class MemberInfo {
		@Schema(description = "회원 ID", example = "1")
		private Long id;

		@Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
		private String profileImage;

		@Schema(description = "이름", example = "동규")
		private String firstName;

		@Schema(description = "성", example = "박")
		private String lastName;

		@Schema(description = "선호하는 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime preferredWakeupTime;

		@Schema(description = "성공한 게임 횟수", example = "5")
		private Integer successGameCount;

		@Schema(description = "속한 그룹 목록")
		private GroupResponseDto.GroupListResponseDTO groups;
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED) // access level 수정
	@AllArgsConstructor
	@Schema(description = "회원 요약 정보")
	public static class MemberSummaryDTO {
		@Schema(description = "회원 ID", example = "1")
		private Long id;

		@Schema(description = "이름", example = "동규")
		private String firstName;

		@Schema(description = "성", example = "박")
		private String lastName;

		@Schema(description = "이메일", example = "test@example.com")
		private String email;

	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "회원 요약 정보 리스트")
	public static class MemberListResponseDTO {
		@Schema(description = "전체 회원 수", example = "5")
		private int totalCount;

		@Schema(description = "회원 목록")
		private List<MemberSummaryDTO> members;
	}
}