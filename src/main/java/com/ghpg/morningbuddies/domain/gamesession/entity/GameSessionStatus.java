package com.ghpg.morningbuddies.domain.gamesession.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameSessionStatus {

	READY("준비중"),
	STARTED("진행중"),
	ENDED("종료됨");

	private final String description;

	public static GameSessionStatus from(String description) {
		for (GameSessionStatus status : GameSessionStatus.values()) {
			if (status.getDescription().equals(description)) {
				return status;
			}
		}
		return null;
	}
}
