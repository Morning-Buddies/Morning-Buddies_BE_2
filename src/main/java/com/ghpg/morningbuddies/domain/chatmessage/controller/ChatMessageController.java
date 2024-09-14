package com.ghpg.morningbuddies.domain.chatmessage.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageResponseDto;
import com.ghpg.morningbuddies.domain.chatmessage.service.ChatMessageCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

	private final ChatMessageCommandService chatMessageCommandService;

	@MessageMapping("/chat.sendMessage/{groupId}/{memberId}")
	@SendTo("/sub/chat/{groupId}")
	public ChatMessageResponseDto.Message sendMessage(
		@DestinationVariable Long memberId,
		@DestinationVariable Long groupId,
		@Payload ChatMessageRequestDto.Message chatMessage) {

		log.info("sendMessage: {}", chatMessage);

		return chatMessageCommandService.saveAndConvert(memberId, groupId, chatMessage);
	}

	@MessageMapping("/chat.addUser/{groupId}/{memberId}")
	@SendTo("/sub/chat/{groupId}")
	public ChatMessageResponseDto.Message addUser(
		@DestinationVariable Long memberId,
		@DestinationVariable Long groupId,
		@Payload ChatMessageRequestDto.Message chatMessage) {
		// 여기에 새 사용자가 채팅방에 입장했을 때의 로직을 추가할 수 있습니다.
		// 예를 들어, 입장 메시지를 보내거나 사용자 목록을 업데이트하는 등의 작업을 수행할 수 있습니다.

		return chatMessageCommandService.addUserToGroup(memberId, groupId, chatMessage);
	}

	@MessageMapping("/chat.removeUser/{groupId}/{memberId}")
	@SendTo("/sub/chat/{groupId}")
	public ChatMessageResponseDto.Message removeUser(
		@DestinationVariable Long memberId,
		@DestinationVariable Long groupId,
		@Payload ChatMessageRequestDto.Message chatMessage) {
		// 여기에 사용자가 채팅방을 나갔을 때의 로직을 추가할 수 있습니다.
		// 예를 들어, 퇴장 메시지를 보내거나 사용자 목록을 업데이트하는 등의 작업을 수행할 수 있습니다.
		return chatMessageCommandService.removeUserFromGroup(memberId, groupId, chatMessage);
	}
}