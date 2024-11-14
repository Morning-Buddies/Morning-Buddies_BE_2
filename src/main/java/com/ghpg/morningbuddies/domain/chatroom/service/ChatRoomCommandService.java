package com.ghpg.morningbuddies.domain.chatroom.service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;

public interface ChatRoomCommandService {

	// 채팅방 생성
	void createNewChatRoom(Groups group, Member member);

	void leaveChatRoom(Long chatRoomId, Long memberId);
}
