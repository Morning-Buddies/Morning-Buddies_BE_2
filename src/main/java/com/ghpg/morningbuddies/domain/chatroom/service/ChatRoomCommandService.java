package com.ghpg.morningbuddies.domain.chatroom.service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;

public interface ChatRoomCommandService {

    // 채팅방 생성
    ChatRoomRequestDto createChatRoom(Long groupId, Member member);
}
