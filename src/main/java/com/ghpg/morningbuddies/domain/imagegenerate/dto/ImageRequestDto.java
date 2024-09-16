package com.ghpg.morningbuddies.domain.imagegenerate.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

public class ImageRequestDto {

	@Getter
	public static class GeneratePuzzleImage {

		@NotEmpty
		private String prompt;
	}
}
