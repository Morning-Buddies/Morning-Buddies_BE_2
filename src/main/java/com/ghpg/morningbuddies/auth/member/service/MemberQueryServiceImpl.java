package com.ghpg.morningbuddies.auth.member.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ghpg.morningbuddies.domain.chatroom.ChatRoom;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomRequestDto;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.global.exception.member.MemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.refresh.RefreshException;
import com.ghpg.morningbuddies.global.security.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final MemberRepository memberRepository;

	@Override
	public MemberResponseDto.MemberInfo getMemberInfo(String refreshToken) {
		Member foundMember = refreshTokenRepository.findByRefreshWithMemberAndGroup(refreshToken)
			.orElseThrow(() -> new RefreshException(GlobalErrorCode.INVALID_TOKEN))
			.getMember();

		List<Groups> foundGroups = foundMember.getGroups();

		// 그룹 총 성공 횟수를 멤버의 회원 정보에 저장
		int totalSuccessCount = Optional.ofNullable(foundGroups)
			.map(groups -> groups.stream()
				.mapToInt(Groups::getSuccessCount)
				.sum())
			.orElse(0);

		// 그룹 정보 저장
		List<GroupResponseDto.GroupInfo> groupInfos = new ArrayList<>();

		// 그룹 정보가 존재할 경우 그룹 정보 저장
		if (foundGroups != null) {
			for (Groups foundGroup : foundGroups) {
				groupInfos.add(GroupResponseDto.GroupInfo.builder()
					.name(foundGroup.getGroupName())
					.wakeupTime(foundGroup.getWakeupTime())
					.build());
			}
		}

		return MemberResponseDto.MemberInfo.builder()
			.id(foundMember.getId())
			.profileImage(foundMember.getProfileImageUrl())
			.firstName(foundMember.getFirstName())
			.lastName(foundMember.getLastName())
			.preferredWakeupTime(foundMember.getPreferredWakeupTime())
			.successGameCount(totalSuccessCount)
			.groups(groupInfos)
			.build();
	}

	@Override
	public List<GroupResponseDto.GroupInfo> getMyGroups() {
		Member currentMember = memberRepository.findGroupsByEmail(SecurityUtil.getCurrentMemberEmail())
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
	public List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatroomsByMemberId(Long memberId){
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
