package com.ghpg.morningbuddies.domain.groups.converter;

import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDTO;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;

public class GroupConverter {

	public static GroupResponseDTO.SearchedGroupInfo convertToSearchedGroupInfo(Groups group) {

		return GroupResponseDTO.SearchedGroupInfo.builder()
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
