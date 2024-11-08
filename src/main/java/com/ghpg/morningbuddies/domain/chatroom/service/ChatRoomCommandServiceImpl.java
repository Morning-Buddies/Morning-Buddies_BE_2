package com.ghpg.morningbuddies.domain.chatroom.service;

import org.springframework.stereotype.Service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberChatRoom;
import com.ghpg.morningbuddies.auth.member.repository.MemberChatRoomRepository;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.mapper.ChatRoomMapper;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

	private final GroupRepository groupRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomRepository memberChatRoomRepository;

	// 채팅방 생성
	@Override
	public void createNewChatRoom(Groups group, Member member) {
		
		ChatRoom chatroom = ChatRoomMapper.toNewChatRoom(group);

		// 채팅방 생성
		chatRoomRepository.save(chatroom);

		// 채팅방 생성시 멤버와 채팅방 연결
		memberChatRoomRepository.save(MemberChatRoom.createMemberChatRoom(member, chatroom));

	}
}
