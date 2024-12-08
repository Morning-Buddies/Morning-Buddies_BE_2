package com.ghpg.morningbuddies.domain.membergroup.repository;

import static com.ghpg.morningbuddies.auth.member.entity.QMember.*;
import static com.ghpg.morningbuddies.domain.groups.entity.QGroups.*;
import static com.ghpg.morningbuddies.domain.membergroup.entity.QMemberGroup.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberGroupRepositoryImpl implements MemberGroupRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<MemberGroup> findAllByEmailWithMemberGroups(String email) {
		return jpaQueryFactory
			.selectFrom(memberGroup)
			.leftJoin(memberGroup.group, groups).fetchJoin()
			.leftJoin(memberGroup.member, member).fetchJoin()
			.where(member.email.eq(email))
			.fetch();

	}

	@Override
	public List<MemberGroup> findMemberGroupsByGroupId(Long groupId) {
		return jpaQueryFactory
			.selectFrom(memberGroup)
			.join(memberGroup.member, member).fetchJoin()
			.join(memberGroup.group, groups).fetchJoin()
			.where(groups.id.eq(groupId))
			.fetch();
	}
}
