package com.ghpg.morningbuddies.auth.member.service.query;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.member.MemberException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

	private final MemberJPARepository memberJPARepository;

	@Override
	public MemberResponseDto.MemberInfo getMemberInfo(String email) {
		Member member = memberJPARepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		return MemberResponseDto.MemberInfo.from(member);
	}

	@Override
	public GroupResponseDto.GroupsResponseDto getMemberGroups(String email) {

		Member member = memberJPARepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		return GroupResponseDto.GroupsResponseDto.of(member.getMemberGroups());

	}

	private static @NotNull Set<Groups> getCurrentMembersGroups(Member currentMember) {
		return currentMember.getMemberGroups().stream()
			.map(MemberGroup::getGroup)
			.collect(Collectors.toSet());
	}

	// 회원이 가입한 채팅방 리스트 가져오기
	@Override
	public List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatRoomsByMemberId(Long memberId) {
		List<ChatRoom> chatRooms = memberJPARepository.findAllChatroomsByMemberId(memberId);

		Member member = memberJPARepository.findById(memberId)
			.orElseThrow(() -> new MemberException(ErrorStatus.MEMBER_NOT_FOUND));

		return chatRooms.stream()
			.map(chatRoom -> ChatRoomResponseDto.AllChatRoomByMemberId.builder()
				.id(chatRoom.getId())
				.groupName(chatRoom.getGroup().getGroupName())
				.build())
			.collect(Collectors.toList());
	}
}
