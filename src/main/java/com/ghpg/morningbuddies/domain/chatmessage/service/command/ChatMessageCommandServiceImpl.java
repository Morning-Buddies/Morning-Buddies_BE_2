package com.ghpg.morningbuddies.domain.chatmessage.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.chatmessage.MessageType;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageResponseDto;
import com.ghpg.morningbuddies.domain.chatmessage.repository.ChatMessageJpaRepository;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.repository.ChatRoomJpaRepository;
import com.ghpg.morningbuddies.domain.groups.repository.GroupJPARepository;
import com.ghpg.morningbuddies.global.exception.chatroom.ChatRoomException;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageCommandServiceImpl implements ChatMessageCommandService {

	private final ChatMessageJpaRepository chatMessageRepository;

	private final GroupJPARepository groupJPARepository;

	private final MemberJPARepository memberJPARepository;

	private final ChatRoomJpaRepository chatRoomJpaRepository;

	@Override
	public ChatMessageResponseDto.Message saveAndConvert(Long memberId, Long chatRoomId,
		ChatMessageRequestDto.Message message) {
		ChatRoom currentParticipantChatRoom = chatRoomJpaRepository.findById(chatRoomId)
			.orElseThrow(() -> new ChatRoomException(ErrorStatus.CHATROOM_NOT_FOUND));

		Member currentMember = memberJPARepository.findById(memberId)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		ChatMessage sentMessage = ChatMessage.builder()
			.message(message.getMessage())
			.messageType(MessageType.CHAT)
			.chatRoom(currentParticipantChatRoom)
			.sender(currentMember)
			.sendTime(message.getTime())
			.build();

		ChatMessage savedMessage = chatMessageRepository.save(sentMessage);

		ChatMessageResponseDto.Sender sender = ChatMessageResponseDto.Sender.builder()
			.memberId(currentMember.getId())
			.name(currentMember.getFirstName() + " " + currentMember.getLastName())
			.profileImageUrl(currentMember.getProfileImageUrl())
			.build();

		return ChatMessageResponseDto.Message.builder()
			.chatRoomId(currentParticipantChatRoom.getId())
			.type(MessageType.CHAT)
			.sender(sender)
			.message(savedMessage.getMessage())
			.time(savedMessage.getSendTime())
			.build();

	}

	@Override
	public ChatMessageResponseDto.Message addUserToGroup(Long memberId, Long groupId,
		ChatMessageRequestDto.Message message) {
		// Groups group = groupRepository.findById(groupId)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));
		//
		// Member member = memberRepository.findById(memberId)
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// // Add user to group logic here
		// // This might involve updating a GroupMember entity or similar
		// group.addMember(member);
		// groupRepository.save(group);
		//
		// ChatMessage joinMessage = ChatMessage.builder()
		// 	.message(member.getFirstName() + " " + member.getLastName() + " has joined the group.")
		// 	.messageType(MessageType.ENTER)
		// 	.chatRoom(group.getChatRoom())
		// 	.sender(member)
		// 	.sendTime(message.getTime())
		// 	.build();
		//
		// ChatMessage savedMessage = chatMessageRepository.save(joinMessage);
		//
		// return createChatMessageResponseDto(savedMessage);

		return null;
	}

	@Override
	public ChatMessageResponseDto.Message removeUserFromGroup(Long memberId, Long groupId,
		ChatMessageRequestDto.Message message) {
		// Groups group = groupRepository.findById(groupId)
		// 	.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));
		//
		// Member member = memberRepository.findById(memberId)
		// 	.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));
		//
		// // Remove user from group logic here
		// // This might involve updating a GroupMember entity or similar
		//
		// group.removeMember(member);
		// groupRepository.save(group);
		//
		// ChatMessage leaveMessage = ChatMessage.builder()
		// 	.message(member.getFirstName() + " " + member.getLastName() + " has left the group.")
		// 	.messageType(MessageType.LEAVE)
		// 	.chatRoom(group.getChatRoom())
		// 	.sender(member)
		// 	.sendTime(message.getTime())
		// 	.build();
		//
		// ChatMessage savedMessage = chatMessageRepository.save(leaveMessage);
		//
		// return createChatMessageResponseDto(savedMessage);

		// This method is missing a return statement. I will add a return statement that returns null.
		return null;
	}

	// private ChatMessageResponseDto.Message createChatMessageResponseDto(ChatMessage chatMessage) {
	// 	ChatMessageResponseDto.Sender sender = ChatMessageResponseDto.Sender.builder()
	// 		.memberId(chatMessage.getSender().getId())
	// 		.name(chatMessage.getSender().getFirstName() + " " + chatMessage.getSender().getLastName())
	// 		.profileImageUrl(chatMessage.getSender().getProfileImageUrl())
	// 		.build();
	//
	// 	return ChatMessageResponseDto.Message.builder()
	// 		.chatRoomId(chatMessage.getChatRoom().getId())
	// 		.type(chatMessage.getMessageType())
	// 		.sender(sender)
	// 		.message(chatMessage.getMessage())
	// 		.time(chatMessage.getSendTime())
	// 		.build();
	// }

}
