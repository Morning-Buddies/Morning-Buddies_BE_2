package com.ghpg.morningbuddies.auth.member.service.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.mapper.MemberMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import com.ghpg.morningbuddies.global.exception.refresh.RefreshException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;
	private final MemberMapper memberMapper;

	@Override
	public MemberResponseDto.MemberInfo getMyInfo() {
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();

		Member currentMember = memberRepository.findMemberAndGroupsByEmail(currentUserEmail)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		return memberMapper.toMemberInfo(currentMember);
	}

	@Override
	public List<GroupResponseDto.GroupInfo> getMyGroups() {
		Member currentMember = memberRepository.findMemberAndGroupsByEmail(SecurityUtil.getCurrentUserEmail())
			.orElseThrow(() -> new RefreshException(GlobalErrorCode.INVALID_TOKEN));

		List<Groups> foundGroups = currentMember.getGroups();

		List<GroupResponseDto.GroupInfo> groupInfos = new ArrayList<>();

		if (foundGroups != null) {
			for (Groups foundGroup : foundGroups) {
				groupInfos.add(GroupResponseDto.GroupInfo.builder()
					.id(foundGroup.getId())
					.name(foundGroup.getGroupName())
					.wakeupTime(foundGroup.getWakeupTime())
					.build());
			}
		}

		return groupInfos;
	}

	// 회원이 가입한 채팅방 리스트 가져오기
	@Override
	public List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatroomsByMemberId(Long memberId) {
		List<ChatRoom> chatRooms = memberRepository.findAllChatroomsByMemberId(memberId);

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberException(GlobalErrorCode.MEMBER_NOT_FOUND));

		return chatRooms.stream()
			.map(chatRoom -> ChatRoomResponseDto.AllChatRoomByMemberId.builder()
				.id(chatRoom.getId())
				.groupName(chatRoom.getGroup().getGroupName())
				.build())
			.collect(Collectors.toList());
	}
}
