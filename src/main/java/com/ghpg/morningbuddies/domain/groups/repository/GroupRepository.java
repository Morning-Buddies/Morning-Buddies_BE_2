package com.ghpg.morningbuddies.domain.groups.repository;

import java.util.Optional;

import com.ghpg.morningbuddies.domain.groups.entity.Groups;

public interface GroupRepository {
	Optional<Groups> findGroupAndMemberGroupsAndGroupByGroupId(Long groupId);
}
