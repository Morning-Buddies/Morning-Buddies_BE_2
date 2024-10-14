package com.ghpg.morningbuddies.domain.chatroom.controller;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;

    @PostMapping("/create")
    public CommonResponse<ChatRoomRequestDto> createChatRoom(@RequestParam Long groupId, @RequestBody Member member) {
        ChatRoomRequestDto requestDto = chatRoomCommandService.createChatRoom(groupId, member);

        return CommonResponse.onSuccess(requestDto);
    }

}
