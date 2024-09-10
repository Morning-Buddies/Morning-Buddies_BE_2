package com.ghpg.morningbuddies.domain.chatmessage.dto;

import java.time.LocalDateTime;

import com.ghpg.morningbuddies.domain.chatmessage.MessageType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatMessageResponseDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Message {
		private MessageType type; // 메세지 타입
		private Long groupId;// 방 번호
		private Sender sender;//채팅을 보낸 사람
		private String message;// 메세지
		private LocalDateTime time; // 채팅 발송 시간
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class Sender {
		private Long memberId;
		private String name;
		private byte[] profileImage;
	}
}
