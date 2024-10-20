package com.ghpg.morningbuddies.domain.chatmessage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	
}
