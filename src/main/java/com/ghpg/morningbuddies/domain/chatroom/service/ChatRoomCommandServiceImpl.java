package com.ghpg.morningbuddies.domain.chatroom.service;

import org.springframework.stereotype.Service;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomRepository;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;
import com.ghpg.morningbuddies.domain.memberchatroom.repository.MemberChatRoomJPARepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomCommandServiceImpl implements ChatRoomCommandService {

	private final GroupJPARepository groupJPARepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberChatRoomJPARepository memberChatRoomJPARepository;
	private final MemberJPARepository memberJPARepository;

	// 채팅방 생성
	@Override
	public void createNewChatRoom(Groups group, Member member) {

		ChatRoom newChatRoom = ChatRoom.createChatRoom(group);

		MemberChatRoom memberChatRoom = MemberChatRoom.createMemberChatRoom(member, newChatRoom);

		newChatRoom.addMemberChatRoom(memberChatRoom);
		// 채팅방 생성
		chatRoomRepository.save(newChatRoom);

	}

	@Override
	public void leaveChatRoom(Long chatRoomId, Long memberId) {
		// // 1. 채팅방과 멤버 조회
		// ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
		// 	.orElseThrow(() -> new ChatRoomException(GlobalErrorCode.CHATROOM_NOT_FOUND));
		//
		// Member member = memberRepository.findById(memberId)
		// 	.orElseThrow(() -> new ChatRoomException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// // 2. 채팅방에서 멤버 제거
		// chatRoom.removeMember(member);
		//
		// log.info("Member {} left chatroom {}", memberId, chatRoomId);
	}
}
