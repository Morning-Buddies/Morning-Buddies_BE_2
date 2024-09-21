package com.ghpg.morningbuddies.global.exception.puzzle;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class PuzzleException extends GeneralException {
	public PuzzleException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
