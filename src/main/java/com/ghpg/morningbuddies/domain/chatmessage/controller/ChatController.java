package com.ghpg.morningbuddies.domain.chatmessage.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;

@Controller
public class ChatController {

	@MessageMapping("/chat.sendMessage")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		return chatMessage;
	}

	@MessageMapping("/chat.addUser")
	@SendTo("/topic/public")
	public ChatMessage addUser(@Payload ChatMessage chatMessage) {
		// 사용자가 채팅방에 입장했을 때의 로직
		return chatMessage;
	}
}