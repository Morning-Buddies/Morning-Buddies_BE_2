package com.ghpg.morningbuddies.domain.groups.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupRequestDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(name = "CreateGroupDto", description = "그룹 생성 요청 데이터")
	public static class CreateGroupDto {
		@NotBlank
		@Schema(description = "그룹 이름", example = "아침 일찍 일어나기", type = "string")
		private String groupName;

		@NotNull
		@Schema(description = "기상 시간", example = "06:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime wakeUpTime;

		@Min(value = 1)
		@Max(value = 10)
		@NotNull
		@Schema(description = "최대 참가자 수", example = "5", type = "integer", minimum = "1", maximum = "10")
		private Integer maxParticipantCount;

		@NotBlank
		@Size(max = 500)
		@Schema(description = "그룹 설명", example = "아침 일찍 일어나서 운동하고 싶은 사람들을 위한 그룹입니다.", type = "string")
		private String description;
	}

	// 그룹 수정 DTO
	@Getter
	public static class UpdateGroupDTO {

		@NotBlank
		private String groupName;

		@NotNull
		private LocalTime wakeUpTime;

		@Min(value = 1)
		@Max(value = 10)
		@NotNull
		private Integer maxParticipantCount;

		@NotBlank
		@Size(max = 500)
		private String description;

	}
}
