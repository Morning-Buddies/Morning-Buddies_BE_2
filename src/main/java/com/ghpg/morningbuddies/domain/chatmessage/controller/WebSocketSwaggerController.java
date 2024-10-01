package com.ghpg.morningbuddies.domain.chatmessage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/websocket")
@Tag(name = "WebSocket API", description = "WebSocket 채팅 API")
public class WebSocketSwaggerController {

	@Operation(summary = "WebSocket 연결", description = "채팅을 위한 WebSocket 연결을 설정합니다.")
	@GetMapping("/chat/{groupId}/{memberId}")
	public void connectWebSocket(@PathVariable Long groupId, @PathVariable Long memberId) {
		// 이 메서드는 실제로 호출되지 않습니다. Swagger 문서화를 위한 더미 메서드입니다.
	}

	@Operation(summary = "메시지 전송", description = "WebSocket을 통해 채팅 메시지를 전송합니다.")
	@GetMapping("/chat/send")
	public void sendMessage(ChatMessageRequestDto.Message message) {
		// 이 메서드는 실제로 호출되지 않습니다. Swagger 문서화를 위한 더미 메서드입니다.
	}

	@Operation(summary = "사용자 추가", description = "채팅 그룹에 사용자를 추가합니다.")
	@GetMapping("/chat/addUser")
	public void addUser(ChatMessageRequestDto.Message message) {
		// 이 메서드는 실제로 호출되지 않습니다. Swagger 문서화를 위한 더미 메서드입니다.
	}

	@Operation(summary = "사용자 제거", description = "채팅 그룹에서 사용자를 제거합니다.")
	@GetMapping("/chat/removeUser")
	public void removeUser(ChatMessageRequestDto.Message message) {
		// 이 메서드는 실제로 호출되지 않습니다. Swagger 문서화를 위한 더미 메서드입니다.
	}
}