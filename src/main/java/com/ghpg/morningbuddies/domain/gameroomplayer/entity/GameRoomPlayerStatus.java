package com.ghpg.morningbuddies.domain.gameroomplayer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameRoomPlayerStatus {

	READY("준비중"),
	PLAYING("게임중"),
	FINISHED("게임종료"),
	QUIT("나감");

	private final String description;

	public static GameRoomPlayerStatus of(String description) {
		for (GameRoomPlayerStatus status : values()) {
			if (status.getDescription().equals(description)) {
				return status;
			}
		}
		return null;
	}

}
