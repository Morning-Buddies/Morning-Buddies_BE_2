package com.ghpg.morningbuddies.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
