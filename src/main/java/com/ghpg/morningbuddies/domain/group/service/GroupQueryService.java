package com.ghpg.morningbuddies.domain.group.service;

import org.springframework.data.domain.Pageable;

import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

public interface GroupQueryService {

	// 그룹 정보 가져오기
	GroupResponseDto.GroupDetailDTO getGroupDetailById(Long groupId);

	// 그룹 검색 결과 가져오기
	GroupResponseDto.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable);
}
