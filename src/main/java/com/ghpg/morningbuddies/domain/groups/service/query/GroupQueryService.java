package com.ghpg.morningbuddies.domain.groups.service.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ghpg.morningbuddies.domain.groups.dto.GroupResponseDTO;

public interface GroupQueryService {

	// 그룹 정보 가져오기
	GroupResponseDTO.GroupDetailDTO getGroupDetailById(Long groupId);

	// 그룹 검색 결과 가져오기
	GroupResponseDTO.SearchedGroupInfoList getSearchedGroupInfoList(String keyword, Pageable pageable);

	// 그룹 가입 요청 리스트
	List<GroupResponseDTO.JoinRequestDTO> findByGroupAndStatus(Long groupId);

	// 생성된 모든 그룹 리스트 가져오기
	Page<GroupResponseDTO.GroupSummaryDTO> getAllGroups(Integer page, Integer size);

	// 핫한 그룹 기준
	Page<GroupResponseDTO.GroupSummaryDTO> getHotGroups(Integer page, Integer size);

	// 일찍 일어나는 그룹 기준
	Page<GroupResponseDTO.GroupSummaryDTO> getEarlyMorningGroups(Integer page, Integer size);

	// 늦게 일어나는 그룹 기준
	Page<GroupResponseDTO.GroupSummaryDTO> getGroupsByLateEvening(Integer page, Integer size);
}
