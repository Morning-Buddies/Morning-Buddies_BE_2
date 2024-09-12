package com.ghpg.morningbuddies.domain.chatmessage.service;

import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageResponseDto;

public interface ChatMessageCommandService {
	ChatMessageResponseDto.Message saveAndConvert(Long memberId, Long groupId, ChatMessageRequestDto.Message message);

	ChatMessageResponseDto.Message addUserToGroup(Long memberId, Long groupId,
		ChatMessageRequestDto.Message chatMessage);

	ChatMessageResponseDto.Message removeUserFromGroup(Long memberId, Long groupId,
		ChatMessageRequestDto.Message chatMessage);
}
