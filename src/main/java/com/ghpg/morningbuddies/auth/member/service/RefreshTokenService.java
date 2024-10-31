package com.ghpg.morningbuddies.auth.member.service;

public interface RefreshTokenService {

	void saveNewRefreshToken(String email, String refreshToken);
	
}
