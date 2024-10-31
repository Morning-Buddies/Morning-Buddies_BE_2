package com.ghpg.morningbuddies.auth.member.mapper;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.RefreshToken;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenMapper {

	public static RefreshToken toRefreshToken(String refreshToken, String email, Member member) {
		return RefreshToken.builder()
			.email(email)
			.refreshToken(refreshToken)
			.expiration(JwtUtil.REFRESH_TOKEN_EXPIRATION_MS)
			.member(member)
			.build();
	}
}
