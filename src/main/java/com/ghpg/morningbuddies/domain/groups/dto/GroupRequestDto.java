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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupRequestDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GroupCommand {
		@NotBlank
		@Schema(description = "그룹 이름", example = "아침 일찍 일어나기")
		private String groupName;

		@NotNull
		@Schema(description = "기상 시간", example = "06:00")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime wakeUpTime;

		@Min(value = 1)
		@Max(value = 10)
		@NotNull
		@Schema(description = "최대 참가자 수", example = "5")
		private Integer maxParticipantCount;

		@NotBlank
		@Size(max = 500)
		@Schema(description = "그룹 설명")
		private String description;
	}
}
