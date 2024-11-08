package com.ghpg.morningbuddies.auth.member.service.command;

public interface RefreshTokenCommandService {

	void saveNewRefreshToken(String email, String refreshToken);

	void removeRefreshToken(String refreshToken);
}
