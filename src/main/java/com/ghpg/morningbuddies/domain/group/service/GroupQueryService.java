package com.ghpg.morningbuddies.domain.group.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

import java.util.List;

public interface GroupQueryService {

	// 그룹 정보 가져오기
	GroupResponseDto.GroupDetailDTO getGroupDetailById(Long groupId);

	// 그룹 검색 결과 가져오기
	GroupResponseDto.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable);

	// 그룹 가입 요청 리스트
	List<GroupResponseDto.JoinRequestDTO> findByGroupAndStatus(Long groupId);

	// 생성된 모든 그룹 리스트 가져오기
	Page<GroupResponseDto.GroupSummaryDTO> getAllGroups(Integer page, Integer size);

	// 핫한 그룹 기준
	Page<GroupResponseDto.GroupSummaryDTO> getHotGroups(Integer page, Integer size);

	// 일찍 일어나는 그룹 기준
	Page<GroupResponseDto.GroupSummaryDTO> getEarlyMorningGroups(Integer page, Integer size);

	// 늦게 일어나는 그룹 기준
	Page<GroupResponseDto.GroupSummaryDTO> getGroupsByLateEvening(Integer page, Integer size);
}
