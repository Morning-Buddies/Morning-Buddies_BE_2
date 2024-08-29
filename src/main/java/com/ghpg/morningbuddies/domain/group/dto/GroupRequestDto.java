package com.ghpg.morningbuddies.domain.group.dto;

import java.time.LocalTime;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class GroupRequestDto {

	@Getter
	public static class CreateGroupDto {

		@NotNull
		private Integer groupId;

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

		@Lob
		private String imageUrl;
	}
}
