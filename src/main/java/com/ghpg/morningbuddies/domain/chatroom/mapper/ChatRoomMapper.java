package com.ghpg.morningbuddies.domain.chatroom.mapper;

import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.group.entity.Groups;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMapper {

	public static ChatRoom toNewChatRoom(Groups group) {
		return ChatRoom.builder()
			.group(group)
			.build();
	}
}
