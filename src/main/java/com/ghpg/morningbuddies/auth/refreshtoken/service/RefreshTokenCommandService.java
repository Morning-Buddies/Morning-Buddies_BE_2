package com.ghpg.morningbuddies.auth.refreshtoken.service;

public interface RefreshTokenCommandService {

	void saveNewRefreshToken(String email, String refreshToken);

	void removeRefreshToken(String refreshToken);
}
