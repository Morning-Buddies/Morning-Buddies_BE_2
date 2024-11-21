package com.ghpg.morningbuddies.auth.member.repository;

import static com.ghpg.morningbuddies.auth.member.entity.QMember.*;
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
	public Optional<Member> findMemberAndGroupsByEmail(String email) {
		return Optional.ofNullable(jpaQueryFactory
			.selectFrom(member)
			.leftJoin(member.memberGroups, memberGroup).fetchJoin()
			.leftJoin(memberGroup.group).fetchJoin()
			.leftJoin(member.groups).fetchJoin()  // groups 컬렉션에 대한 fetch join 추가
			.where(member.email.eq(email))
			.fetchOne());
	}

}
