package com.ghpg.morningbuddies.auth.member.service.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberJPARepository;
import com.ghpg.morningbuddies.auth.refreshtoken.repository.RefreshTokenJPARepository;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.exception.refresh.RefreshTokenException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

	private final RefreshTokenJPARepository refreshTokenJPARepository;
	private final MemberJPARepository memberJPARepository;

	@Override
	public MemberResponseDto.MemberInfo getMyInfo() {
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();

		Member currentMember = memberJPARepository.findMemberAndGroupsByEmail(currentUserEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		return MemberResponseDto.MemberInfo.of(currentMember);
	}

	@Override
	public GroupResponseDto.GroupListResponseDTO getMyGroups() {
		Member currentMember = memberJPARepository.findMemberAndGroupsByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new RefreshTokenException(GlobalErrorCode.INVALID_TOKEN));

		List<Groups> foundGroups = currentMember.getGroups();

		return null;
	}

	// 회원이 가입한 채팅방 리스트 가져오기
	@Override
	public List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatroomsByMemberId(Long memberId) {
		List<ChatRoom> chatRooms = memberJPARepository.findAllChatroomsByMemberId(memberId);

		Member member = memberJPARepository.findById(memberId)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		return chatRooms.stream()
			.map(chatRoom -> ChatRoomResponseDto.AllChatRoomByMemberId.builder()
				.id(chatRoom.getId())
				.groupName(chatRoom.getGroup().getGroupName())
				.build())
			.collect(Collectors.toList());
	}
}
