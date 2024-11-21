package com.ghpg.morningbuddies.global.exception.common.code;

import com.ghpg.morningbuddies.global.exception.common.Reason;

public interface BaseCode {
	Reason getReason();

	Reason getReasonHttpStatus();
}
