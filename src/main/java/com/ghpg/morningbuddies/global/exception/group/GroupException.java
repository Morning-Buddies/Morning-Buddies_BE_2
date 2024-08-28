package com.ghpg.morningbuddies.global.exception.group;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class GroupException extends GeneralException {

    public GroupException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
