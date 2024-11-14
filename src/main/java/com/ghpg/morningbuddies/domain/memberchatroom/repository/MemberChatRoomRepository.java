package com.ghpg.morningbuddies.domain.memberchatroom.repository;

import java.util.Optional;

import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;

public interface MemberChatRoomRepository {

	Optional<MemberChatRoom> findByMemberChatRoomIdWithMemberAndChatRoom(Long memberChatRoomId);
	
}
