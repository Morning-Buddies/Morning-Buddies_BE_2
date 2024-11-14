package com.ghpg.morningbuddies.domain.membergroup.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;

public interface MemberGroupJPARepository extends JpaRepository<MemberGroup, Long> {

	Optional<MemberGroup> findByMemberAndGroup(Member member, Groups group);
}
