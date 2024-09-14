package com.ghpg.morningbuddies.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ghpg.morningbuddies.auth.member.dto.CustomMemberDetails;
import com.ghpg.morningbuddies.global.exception.common.GeneralException;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

public class SecurityUtil {
	public static String getCurrentMemberEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new GeneralException(GlobalErrorCode.AUTHENTICATION_DENIED);
		}

		CustomMemberDetails customMemberDetails = (CustomMemberDetails)authentication.getPrincipal();
		return customMemberDetails.getEmail();
	}

}
