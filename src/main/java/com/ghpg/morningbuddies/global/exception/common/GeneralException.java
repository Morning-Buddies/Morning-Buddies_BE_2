package com.ghpg.morningbuddies.global.exception.common;

import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

	private final BaseErrorCode errorCode;
	private final Object data;

	public GeneralException(BaseErrorCode errorCode) {
		this.errorCode = errorCode;
		this.data = null;
	}

	public ErrorReason getErrorReason() {
		return this.errorCode.getReason();
	}

	public ErrorReason getErrorReasonHttpStatus() {
		return this.data != null ?
			this.errorCode.getReasonHttpStatus(this.data) :
			this.errorCode.getReasonHttpStatus();
	}
}