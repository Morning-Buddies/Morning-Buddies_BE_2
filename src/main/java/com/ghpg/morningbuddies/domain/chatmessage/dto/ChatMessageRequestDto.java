package com.ghpg.morningbuddies.domain.chatmessage.dto;

import java.time.LocalDateTime;

import com.ghpg.morningbuddies.domain.chatmessage.MessageType;

import lombok.Getter;

public class ChatMessageRequestDto {
	@Getter
	public static class Message {
		private MessageType type; // 메세지 타입
		private String message;// 메세지
		private LocalDateTime time; // 채팅 발송 시간
	}

}

