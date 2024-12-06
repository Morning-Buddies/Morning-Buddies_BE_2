package com.ghpg.morningbuddies.auth.member.repository;

import java.util.Optional;

import com.ghpg.morningbuddies.auth.member.entity.Member;

public interface MemberRepository {

	Optional<Member> findByEmailWithMemberGroups(String email);
	
}
