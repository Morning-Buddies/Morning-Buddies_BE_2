package com.ghpg.morningbuddies.global.exception.refresh;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class RefreshTokenException extends GeneralException {
	public RefreshTokenException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
