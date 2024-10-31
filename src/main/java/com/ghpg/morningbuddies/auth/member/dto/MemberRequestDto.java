package com.ghpg.morningbuddies.auth.member.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ghpg.morningbuddies.auth.member.validation.annotation.ExistingEmail;
import com.ghpg.morningbuddies.auth.member.validation.annotation.UniqueEmail;
import com.ghpg.morningbuddies.auth.member.validation.annotation.UniquePhoneNumber;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
		@Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")  // 추가
		@Schema(description = "이메일", example = "test@example.com")
		@UniqueEmail
		private String email;

		@NotEmpty
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
		@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8자 이상)", example = "password123!")
		private String password;

		@NotEmpty
		@Schema(description = "이름", example = "동규")
		private String firstName;

		@NotEmpty
		@Schema(description = "성", example = "박")
		private String lastName;

		@NotNull
		@Schema(description = "선호하는 기상 시간", example = "07:00", type = "string")
		@JsonFormat(pattern = "HH:mm")
		private LocalTime preferredWakeupTime;

		@NotEmpty
		@UniquePhoneNumber
		@Pattern(regexp = "^\\d{10,11}$")
		@Schema(description = "전화번호 (10-11자리 숫자)", example = "01012345678")
		private String phoneNumber;

	}

	@Getter
	public static class LoginDto {

		@NotEmpty
		@Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$")  // 추가
		@Schema(description = "이메일", example = "test@example.com")
		@ExistingEmail
		private String email;

		@NotEmpty
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
		@Schema(description = "비밀번호", example = "password123!")
		private String password;

	}

	@Getter
	public static class PasswordDto {

		@NotEmpty
		@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")
		@Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8자 이상)",
			example = "password1234!")
		private String password;
	}

	@Getter
	public static class FcmTokenDto {

		@NotEmpty
		@Size(max = 255)  // 추가
		@Schema(description = "FCM 토큰", example = "fcmToken")
		private String fcmToken;

		@NotEmpty
		@Size(max = 100)  // 추가
		@Schema(description = "디바이스 아이디", example = "deviceId")
		private String deviceId;

	}
}
