package com.ghpg.morningbuddies.domain.groups.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GroupStatus {

	ACTIVE("ACTIVE"),
	INACTIVE("INACTIVE");

	private final String status;

	public static GroupStatus of(String status) {
		for (GroupStatus groupStatus : values()) {
			if (groupStatus.getStatus().equals(status)) {
				return groupStatus;
			}
		}
		return null;
	}
}
