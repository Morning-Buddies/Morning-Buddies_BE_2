package com.ghpg.morningbuddies.auth.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByEmail(String email);

	@EntityGraph(attributePaths = {"groups"})
	Optional<Member> findByEmail(String email);

	@EntityGraph(attributePaths = {"memberGroups", "memberGroups.group"})
	Optional<Member> findMemberAndGroupsByEmail(String email);

	// 해당 그룹에 속한 멤버 가져오기
	@Query("SELECT m FROM Member m JOIN m.memberGroups mg WHERE mg.group.id = :groupId")
	List<Member> findAllMemberByGroupId(@Param("groupId") Long groupId);

	// 회원 아이디로 회원이 가입한 채팅방 가져오기
	@Query("SELECT mcr.chatRoom FROM MemberChatRoom mcr WHERE mcr.member.id = :memberId")
	List<ChatRoom> findAllChatroomsByMemberId(@Param("memberId") Long memberId);

	// 회원 전화번호로 회원 찾기
	Optional<Member> findByPhoneNumber(String phoneNumber);

	// Soft delete된 회원들 중 특정 시간 이전에 수정된 회원들을 실제로 삭제하는 메서드
	@Modifying
	@Query("DELETE FROM Member m WHERE m.isDeleted = true AND m.updatedAt < :beforeDate")
	int deleteAllByIsDeletedTrueAndUpdatedAtBefore(@Param("beforeDate") LocalDateTime beforeDate);

}
