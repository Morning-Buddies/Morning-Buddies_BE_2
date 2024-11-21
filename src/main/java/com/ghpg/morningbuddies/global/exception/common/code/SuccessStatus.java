package com.ghpg.morningbuddies.global.exception.common.code;

import org.springframework.http.HttpStatus;

import com.ghpg.morningbuddies.global.exception.common.Reason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
	// 일반적인 응답
	_OK(HttpStatus.OK, "COMMON200", "성공입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public Reason getReason() {
		return Reason.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build();
	}

	@Override
	public Reason getReasonHttpStatus() {
		return Reason.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build()
			;
	}
}
