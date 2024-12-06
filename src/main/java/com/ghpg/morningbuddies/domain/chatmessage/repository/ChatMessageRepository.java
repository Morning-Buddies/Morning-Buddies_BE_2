package com.ghpg.morningbuddies.domain.chatmessage.repository;

import java.util.List;

import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;

public interface ChatMessageRepository {
	List<ChatMessage> findChatMessageWithCursor(Long chatRoomId, Long lastMessageId, int pageSize);
}
