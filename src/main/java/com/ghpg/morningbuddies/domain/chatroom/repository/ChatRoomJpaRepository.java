package com.ghpg.morningbuddies.domain.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

}
