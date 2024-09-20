package com.ghpg.morningbuddies.domain.group.converter;

import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;

public class GroupConverter {

	public static GroupResponseDto.SearchedGroupInfo convertToSearchedGroupInfo(Groups group) {

		return GroupResponseDto.SearchedGroupInfo.builder()
			.id(group.getId())
			.imageUrl(group.getGroupImageUrl())  // null이 될 수 있음
			.name(group.getGroupName())
			.description(group.getDescription())
			.wakeupTime(group.getWakeupTime())
			.currentParticipantCount(group.getCurrentParticipantCount())
			.maxParticipantCount(group.getMaxParticipantCount())
			.build();
	}
}
