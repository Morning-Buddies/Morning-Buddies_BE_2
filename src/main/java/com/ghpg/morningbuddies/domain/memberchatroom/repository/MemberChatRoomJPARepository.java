package com.ghpg.morningbuddies.domain.memberchatroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;

public interface MemberChatRoomJPARepository extends JpaRepository<MemberChatRoom, Long> {
	Optional<MemberChatRoom> findByChatRoomAndMember(ChatRoom chatRoom, Member member);
}
