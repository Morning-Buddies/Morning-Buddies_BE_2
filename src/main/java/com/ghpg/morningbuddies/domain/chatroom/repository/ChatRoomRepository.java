package com.ghpg.morningbuddies.domain.chatroom.repository;

import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
