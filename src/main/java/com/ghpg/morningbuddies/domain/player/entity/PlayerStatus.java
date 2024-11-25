package com.ghpg.morningbuddies.domain.player.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerStatus {

	READY("준비중"),
	PLAYING("게임중"),
	FINISHED("게임완료"),
	LEAVE("탈퇴");

	private final String status;

	public static PlayerStatus of(String status) {
		for (PlayerStatus playerStatus : values()) {
			if (playerStatus.getStatus().equals(status)) {
				return playerStatus;
			}
		}
		return null;
	}
}
