package com.ghpg.morningbuddies.domain.gameroom.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameRoomStatus {

	READY("준비중"),
	PLAYING("게임중"),
	FINISHED("게임완료"),
	LEAVE("탈퇴");

	private final String status;

	public static GameRoomStatus of(String status) {
		for (GameRoomStatus gameRoomStatus : values()) {
			if (gameRoomStatus.getStatus().equals(status)) {
				return gameRoomStatus;
			}
		}
		return null;
	}
}
