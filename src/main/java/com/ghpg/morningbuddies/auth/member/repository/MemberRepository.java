package com.ghpg.morningbuddies.auth.member.repository;

import java.util.List;
import java.util.Optional;

import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;
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

	// 회원 아이디로 회원이 가입한 채팅방 가져오기
	@Query("SELECT mcr.chatRoom FROM MemberChatRoom mcr WHERE mcr.member.id = :memberId")
	List<ChatRoom> findAllChatroomsByMemberId(@Param("memberId") Long memberId);
}
