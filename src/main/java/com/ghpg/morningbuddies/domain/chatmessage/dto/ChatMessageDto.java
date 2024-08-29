package com.ghpg.morningbuddies.domain.chatmessage.dto;

public class ChatMessageDto {
	private MessageType type;
	private String content;
	private String sender;
	private String groupId;

	// getters, setters, constructors

	public enum MessageType {
		CHAT,
		JOIN,
		LEAVE
	}
}
