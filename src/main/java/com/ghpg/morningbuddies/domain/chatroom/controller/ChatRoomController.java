package com.ghpg.morningbuddies.domain.chatroom.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomController {

	private final ChatRoomCommandService chatRoomCommandService;

}
