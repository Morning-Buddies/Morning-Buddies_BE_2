package com.ghpg.morningbuddies.auth.member.service.query;

import java.util.List;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

public interface MemberQueryService {
	MemberResponseDto.MemberInfo getMemberInfo(String refreshToken);

	List<GroupResponseDto.GroupInfo> getMyGroups();

	// 회원이 가입한 채팅방 리스트 가져오기
	List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatroomsByMemberId(Long memberId);
}
