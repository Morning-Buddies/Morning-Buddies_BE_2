package com.ghpg.morningbuddies.domain.groups.repository;

import static com.ghpg.morningbuddies.domain.groups.entity.QGroups.*;
import static com.ghpg.morningbuddies.domain.membergroup.entity.QMemberGroup.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Groups> findGroupAndMemberGroupsAndGroupByGroupId(Long groupId) {
		return Optional.ofNullable(
			jpaQueryFactory
				.selectFrom(groups)
				.leftJoin(groups.memberGroups, memberGroup).fetchJoin()
				.leftJoin(memberGroup.member).fetchJoin()
				.where(groups.id.eq(groupId))
				.fetchOne()
		);

	}
}
