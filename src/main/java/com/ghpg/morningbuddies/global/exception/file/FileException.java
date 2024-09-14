package com.ghpg.morningbuddies.global.exception.file;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class FileException extends GeneralException {

	public FileException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
