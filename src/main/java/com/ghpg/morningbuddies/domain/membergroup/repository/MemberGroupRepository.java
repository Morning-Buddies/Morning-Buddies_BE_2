package com.ghpg.morningbuddies.domain.membergroup.repository;

import java.util.List;

import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;

public interface MemberGroupRepository {

	List<MemberGroup> findAllByEmailWithMemberGroups(String email);

	List<MemberGroup> findMemberGroupsByGroupId(Long groupId);

}
