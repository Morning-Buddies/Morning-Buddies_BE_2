package com.ghpg.morningbuddies.domain.gamesession.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameState {

	READY("준비중"),
	PLAYING("게임중"),
	FINISHED("게임완료"),
	LEAVE("탈퇴");

	private final String state;

	public static GameState of(String state) {
		for (GameState gameState : values()) {
			if (gameState.getState().equals(state)) {
				return gameState;
			}
		}
		return null;
	}
}
