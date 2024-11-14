package com.ghpg.morningbuddies.auth.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class ReissueDto {

		private String accessToken;

		private String refreshToken;

		public static ReissueDto from(String accessToken, String refreshToken) {
			return ReissueDto.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
		}

	}
}
