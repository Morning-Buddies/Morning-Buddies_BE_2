package com.ghpg.morningbuddies.global.exception.JwtException;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class JwtException extends GeneralException {
	public JwtException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
