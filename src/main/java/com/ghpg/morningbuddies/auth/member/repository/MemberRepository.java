package com.ghpg.morningbuddies.auth.member.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);

	@EntityGraph(attributePaths = {"groups"})
	Optional<Member> findGroupsByEmail(String email);

	// 해당 그룹에 속한 멤버 가져오기
	@Query("SELECT m FROM Member m JOIN m.memberGroups mg WHERE mg.group.id = :groupId")
	List<Member> findAllMemberByGroupId(@Param("groupId") Long groupId);
}
