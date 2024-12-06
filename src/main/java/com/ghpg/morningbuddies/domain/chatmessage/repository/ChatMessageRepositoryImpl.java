package com.ghpg.morningbuddies.domain.chatmessage.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

	@Override
	public List<ChatMessage> findChatMessageWithCursor(Long chatRoomId, Long lastMessageId, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}
}
