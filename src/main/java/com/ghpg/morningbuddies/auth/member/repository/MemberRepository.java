package com.ghpg.morningbuddies.auth.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.auth.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	@EntityGraph(attributePaths = {"groups"})
	Optional<Member> findGroupsByEmail(String email);
}
