package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequestDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	@Schema(description = "회원가입 요청", title = "회원가입 요청")
	public static class JoinDto {

		@NotEmpty
		private String email;

		@NotEmpty
		private String password;

		@NotEmpty
		private String firstName;

		@NotEmpty
		private String lastName;

		@NotNull
		private LocalTime preferredWakeupTime;

		@NotEmpty
		private String phoneNumber;

	}

	@Getter
	public static class LoginDto {

		@NotEmpty
		private String email;

		@NotEmpty
		private String password;

	}

	@Getter
	public static class PasswordDto {

		@NotEmpty
		private String password;
	}

	@Getter
	public static class FcmTokenDto {
		@NotEmpty
		private String fcmToken;

		@NotEmpty
		private String deviceId;
	}
}
