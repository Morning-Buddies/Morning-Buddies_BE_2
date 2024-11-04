package com.ghpg.morningbuddies.domain.chatroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomRequestDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class ChatRoomInfo {

		@Schema(description = "채팅방 ID", example = "1")
		private Long id;

		@Schema(description = "채팅방 이름", example = "아침형 인간 모임")
		private String name;
	}

}
