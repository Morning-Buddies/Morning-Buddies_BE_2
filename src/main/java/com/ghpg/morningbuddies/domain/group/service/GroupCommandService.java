package com.ghpg.morningbuddies.domain.group.service;

import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

public interface GroupCommandService {

	// 그룹 생성
	GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.CreateGroupDto requestDto, MultipartFile file);

	// 그룹 수정
	GroupResponseDto.GroupDetailDTO updateGroup(Long groupId, GroupRequestDto.UpdateGroupDTO requestDto,
		MultipartFile file);

	// 그룹 삭제
	void deleteGroup(Long groupId);

	// 그룹 가입 요청
	void requestJoinGroup(Long groupId);

	// 그룹 가입 요청 수락 및 그룹 가입
	void acceptJoinGroup(Long groupId, Long requestId);

	// 그룹 가입 요청 거절
	void rejectJoinGroup(Long groupId, Long requestId);

	// 리더 교체
	void changeLeaderAuthority(Long groupId, Long newLeaderId);
}
