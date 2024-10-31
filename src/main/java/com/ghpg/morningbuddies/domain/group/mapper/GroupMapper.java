package com.ghpg.morningbuddies.domain.group.mapper;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.entity.Groups;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupMapper {

	/**
	 * 회원이 가입한 그룹의 성공 게임 횟수를 가져온다.
	 * @param member
	 * @return
	 */
	public static Integer getCountSuccessGame(Member member) {
		return member.getGroups().stream()
			.mapToInt(Groups::getSuccessCount)
			.sum();
	}

	/**
	 * 그룹 정보를 GroupInfo로 변환한다.
	 * @param group
	 * @return
	 */
	public static GroupResponseDto.GroupInfo toGroupInfo(Groups group) {
		return GroupResponseDto.GroupInfo.builder()
			.name(group.getGroupName())
			.wakeupTime(group.getWakeupTime())
			.build();
	}

}
