package com.ghpg.morningbuddies.domain.chatroom.service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService{

    private final GroupRepository groupRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 생성
    @Override
    public ChatRoomRequestDto createChatRoom(Long groupId, Member member){

        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

        ChatRoom chatroom = ChatRoom.builder()
                        .group(group)
                        .build();

        chatRoomRepository.save(chatroom);

        return new ChatRoomRequestDto(chatroom.getId(), chatroom.getGroup().getGroupName());

    }
}
