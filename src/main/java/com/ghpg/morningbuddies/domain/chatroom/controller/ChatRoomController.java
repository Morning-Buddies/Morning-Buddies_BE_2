package com.ghpg.morningbuddies.domain.chatroom.controller;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.service.ChatRoomCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public class ChatRoomController {

    private final ChatRoomCommandService chatRoomCommandService;

    // 채팅방 생성
    @PostMapping("/create")
    @Operation(summary = "채팅방 생성", description = "채팅방이 생성되었습니다.")
    public CommonResponse<ChatRoomRequestDto> createChatRoom(@RequestParam Long groupId, @RequestBody Member member) {
        ChatRoomRequestDto requestDto = chatRoomCommandService.createChatRoom(groupId, member);

        return CommonResponse.onSuccess(requestDto);
    }

}
