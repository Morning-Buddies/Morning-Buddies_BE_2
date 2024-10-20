package com.ghpg.morningbuddies.global.exception.chatroom;

import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.BaseErrorCode;

public class ChatRoomException extends GeneralException {
	public ChatRoomException(BaseErrorCode errorCode) {
		super(errorCode);
	}
}
