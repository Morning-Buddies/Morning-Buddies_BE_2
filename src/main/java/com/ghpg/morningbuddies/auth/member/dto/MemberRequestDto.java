package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class MemberRequestDto {

	@Getter
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
