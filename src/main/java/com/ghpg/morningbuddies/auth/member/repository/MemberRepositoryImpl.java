package com.ghpg.morningbuddies.auth.member.repository;

import static com.ghpg.morningbuddies.auth.member.entity.QMember.*;
import static com.ghpg.morningbuddies.domain.groups.entity.QGroups.*;
import static com.ghpg.morningbuddies.domain.membergroup.entity.QMemberGroup.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Member> findByEmailWithMemberGroups(String email) {
		return Optional.ofNullable(
			jpaQueryFactory.selectFrom(member)
				.leftJoin(member.memberGroups, memberGroup).fetchJoin()
				.leftJoin(memberGroup.group, groups).fetchJoin()
				.where(member.email.eq(email))
				.fetchFirst()
		);
	}

}
