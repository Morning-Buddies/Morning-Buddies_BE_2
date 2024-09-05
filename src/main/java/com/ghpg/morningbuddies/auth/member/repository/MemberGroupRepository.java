package com.ghpg.morningbuddies.auth.member.repository;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberGroupRepository extends JpaRepository<MemberGroup, Long> {

    Optional<MemberGroup> findByMemberAndGroup(Member member, Groups group);
}
