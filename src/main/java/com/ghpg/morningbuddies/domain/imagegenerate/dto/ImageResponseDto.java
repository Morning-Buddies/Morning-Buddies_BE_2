package com.ghpg.morningbuddies.domain.imagegenerate.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageResponseDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class GeneratePuzzleImage {
		private String imageUrl;
	}
}
