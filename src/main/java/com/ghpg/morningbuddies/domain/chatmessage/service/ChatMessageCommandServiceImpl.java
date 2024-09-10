package com.ghpg.morningbuddies.domain.chatmessage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageResponseDto;
import com.ghpg.morningbuddies.domain.chatmessage.repository.ChatMessageRepository;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageCommandServiceImpl implements ChatMessageCommandService {

	private final ChatMessageRepository chatMessageRepository;

	private final GroupRepository groupRepository;

	private final MemberRepository memberRepository;

	@Override
	public ChatMessageResponseDto.Message saveAndConvert(Long memberId, Long groupId,
		ChatMessageRequestDto.Message message) {
		Groups currentParticipatedGroup = groupRepository.findById(groupId)
			.orElseThrow(() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		Member currentMember = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		ChatMessage sendedMessage = ChatMessage.builder()
			.message(message.getMessage())
			.group(currentParticipatedGroup)
			.sender(currentMember)
			.sendTime(message.getTime())
			.build();

		ChatMessage savedMessage = chatMessageRepository.save(sendedMessage);

		ChatMessageResponseDto.Sender sender = ChatMessageResponseDto.Sender.builder()
			.memberId(currentMember.getId())
			.name(currentMember.getFirstName() + " " + currentMember.getLastName())
			.profileImage(currentMember.getProfileImage())
			.build();

		return ChatMessageResponseDto.Message.builder()
			.groupId(currentParticipatedGroup.getId())
			.sender(sender)
			.message(savedMessage.getMessage())
			.time(savedMessage.getSendTime())
			.build();

	}
}
