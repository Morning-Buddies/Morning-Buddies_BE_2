package com.ghpg.morningbuddies.auth.member.service.query;

import java.util.List;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.domain.chatroom.dto.ChatRoomResponseDto;
import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDto;

public interface MemberQueryService {
	MemberResponseDto.MemberInfo getMemberInfo(String email);

	GroupResponseDto.GroupsResponseDto getMemberGroups(String email);

	// 회원이 가입한 채팅방 리스트 가져오기
	List<ChatRoomResponseDto.AllChatRoomByMemberId> findAllChatRoomsByMemberId(Long memberId);
}
