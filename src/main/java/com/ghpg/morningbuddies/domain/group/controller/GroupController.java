package com.ghpg.morningbuddies.domain.group.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.service.GroupCommandService;
import com.ghpg.morningbuddies.domain.group.service.GroupQueryService;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

	private final GroupCommandService groupCommandService;
	private final GroupQueryService groupQueryService;
	private final ObjectMapper objectMapper;

	// 그룹 생성
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CommonResponse<GroupResponseDto.GroupDetailDTO> createGroup(@RequestPart("request") String requestJson,
		@RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

		GroupRequestDto.CreateGroupDto request = objectMapper.readValue(requestJson,
			GroupRequestDto.CreateGroupDto.class);
		GroupResponseDto.GroupDetailDTO group = groupCommandService.createGroup(request, file);

		return CommonResponse.onSuccess(group);
	}

	// 그룹 정보 가져오기
	@GetMapping("/{groupId}")
	public CommonResponse<GroupResponseDto.GroupDetailDTO> getGroupDetailsById(@PathVariable("groupId") Long groupId) {
		GroupResponseDto.GroupDetailDTO group = groupQueryService.getGroupDetailById(groupId);

		return CommonResponse.onSuccess(group);
		}

	// 그룹 정보 수정
	@PatchMapping("/{groupId}")
	public CommonResponse<GroupResponseDto.GroupDetailDTO> updateGroup(@PathVariable("groupId") Long groupId, @RequestPart("request") String requestJson, @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
	  GroupRequestDto.UpdateGroupDTO request = objectMapper.readValue(requestJson, GroupRequestDto.UpdateGroupDTO.class);
	  GroupResponseDto.GroupDetailDTO group = groupCommandService.updateGroup(groupId, request, file);

	  return CommonResponse.onSuccess(group);
	}

	// 그룹 삭제
	@DeleteMapping("/{groupId}")
	public CommonResponse<String> deleteGroup(@PathVariable("groupId") Long groupId) {
		groupCommandService.deleteGroup(groupId);

		return CommonResponse.onSuccess("그룹이 삭제되었습니다.");
	}


	// 그룹 검색 결과 가져오기
	@GetMapping("/search")
	public CommonResponse<GroupResponseDto.SearchedGroupInfoList> searchGroups(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("groupName").ascending());

		return CommonResponse.onSuccess(groupQueryService.getSearchedGroupInfoList(keyword, pageRequest));
	}

	// 그룹 가입 요청
	@PostMapping("/{groupId}/join-request")
	public CommonResponse<String> requestJoinGroup(@PathVariable("groupId") Long groupId){
		groupCommandService.requestJoinGroup(groupId);

		return CommonResponse.onSuccess("그룹 가입 요청을 보냈습니다.");
	}

	// 그룹 가입 요청 리스트
	@GetMapping("/{groupId}/join-request")
	public CommonResponse<List<GroupResponseDto.JoinRequestDTO>> findByGroupAndStatus(@PathVariable("groupId") Long groupId){
		List<GroupResponseDto.JoinRequestDTO> joinRequests = groupCommandService.findByGroupAndStatus(groupId);

		return CommonResponse.onSuccess(joinRequests);
	}

	// 그룹 가입 요청 수락 및 그룹 가입
	@PostMapping("/{groupId}/accept-join/{requestId}")
	public CommonResponse<String> acceptJoinGroup(@PathVariable("groupId") Long groupId, @PathVariable("requestId") Long requestId){
		groupCommandService.acceptJoinGroup(groupId, requestId);

		return CommonResponse.onSuccess("요청을 수락하였습니다.");
	}

}
