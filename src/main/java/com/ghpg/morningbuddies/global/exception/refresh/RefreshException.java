package com.ghpg.morningbuddies.global.exception.refresh;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class RefreshException extends GeneralException {
	public RefreshException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
