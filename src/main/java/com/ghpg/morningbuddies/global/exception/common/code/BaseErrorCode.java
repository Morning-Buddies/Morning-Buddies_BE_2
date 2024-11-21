package com.ghpg.morningbuddies.global.exception.common.code;

import com.ghpg.morningbuddies.global.exception.common.ErrorReason;

public interface BaseErrorCode {

	ErrorReason getReason();

	ErrorReason getReasonHttpStatus();

}