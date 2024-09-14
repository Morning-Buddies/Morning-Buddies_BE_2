package com.ghpg.morningbuddies.global.exception.member;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class MemberException extends GeneralException {

	public MemberException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
