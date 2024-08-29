package com.ghpg.morningbuddies.domain.chatmessage.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageDto;
import com.ghpg.morningbuddies.domain.chatmessage.repository.ChatMessageRepository;

@Service
@Transactional
public class ChatService {

	private final ChatMessageRepository chatMessageRepository;
	private final GroupRepository groupRepository;
	private final MemberRepository memberRepository;

	public ChatService(ChatMessageRepository chatMessageRepository,
		GroupRepository groupRepository,
		MemberRepository memberRepository) {
		this.chatMessageRepository = chatMessageRepository;
		this.groupRepository = groupRepository;
		this.memberRepository = memberRepository;
	}

	public ChatMessage saveChatMessage(ChatMessageDto messageDto) {
		Groups group = groupRepository.findById(Long.parseLong(messageDto.getGroupId()))
			.orElseThrow(() -> new RuntimeException("Group not found"));
		Member member = memberRepository.findByEmail(messageDto.getSender())
			.orElseThrow(() -> new RuntimeException("Member not found"));

		ChatMessage chatMessage = ChatMessage.builder()
			.message(messageDto.getContent())
			.sendTime(LocalDateTime.now())
			.group(group)
			.member(member)
			.build();

		return chatMessageRepository.save(chatMessage);
	}

	public List<ChatMessage> getChatHistory(Long groupId) {
		return chatMessageRepository.findByGroupIdOrderBySendTimeAsc(groupId);
	}
}