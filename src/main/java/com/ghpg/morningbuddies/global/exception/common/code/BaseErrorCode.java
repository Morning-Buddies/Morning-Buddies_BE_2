package com.ghpg.morningbuddies.global.exception.common.code;

import com.ghpg.morningbuddies.global.exception.common.ErrorReason;

public interface BaseErrorCode {

	public ErrorReason getReason();

	public ErrorReason getReasonHttpStatus();
}