package com.ghpg.morningbuddies.auth.member.repository;

import com.ghpg.morningbuddies.auth.member.entity.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
}
