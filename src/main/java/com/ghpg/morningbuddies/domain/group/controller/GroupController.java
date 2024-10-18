package com.ghpg.morningbuddies.domain.group.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.service.GroupCommandService;
import com.ghpg.morningbuddies.domain.group.service.GroupQueryService;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
@Tag(name = "Group", description = "그룹 관련 API")
public class GroupController {

	private final GroupCommandService groupCommandService;
	private final GroupQueryService groupQueryService;
	private final ObjectMapper objectMapper;

	// 그룹 생성
	@PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "그룹 생성", description = "그룹을 생성합니다.")
	public CommonResponse<GroupResponseDto.GroupDetailDTO> createGroup(@RequestPart("request") String requestJson,
		@RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

		GroupRequestDto.CreateGroupDto request = objectMapper.readValue(requestJson,
			GroupRequestDto.CreateGroupDto.class);

		log.info("request: {}", request);

		GroupResponseDto.GroupDetailDTO group = groupCommandService.createGroup(request, file);

		return CommonResponse.onSuccess(group);
	}

	// 그룹 정보 가져오기
	@GetMapping("/{groupId}")
	@Operation(summary = "그룹 정보 가져오기", description = "해당 그룹 정보를 가져옵니다.")
	public CommonResponse<GroupResponseDto.GroupDetailDTO> getGroupDetailsById(@PathVariable("groupId") Long groupId) {
		GroupResponseDto.GroupDetailDTO group = groupQueryService.getGroupDetailById(groupId);

		return CommonResponse.onSuccess(group);
	}

	// 그룹 정보 수정
	@PatchMapping("/{groupId}")
	@Operation(summary = "그룹 정보 수정", description = "해당 그룹 정보를 수정합니다.")
	public CommonResponse<GroupResponseDto.GroupDetailDTO> updateGroup(@PathVariable("groupId") Long groupId,
		@RequestPart("request") String requestJson,
		@RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {
		GroupRequestDto.UpdateGroupDTO request = objectMapper.readValue(requestJson,
			GroupRequestDto.UpdateGroupDTO.class);
		GroupResponseDto.GroupDetailDTO group = groupCommandService.updateGroup(groupId, request, file);

		return CommonResponse.onSuccess(group);
	}

	// 그룹 삭제
	@DeleteMapping("/{groupId}")
	@Operation(summary = "그룹 삭제", description = "해당 그룹을 삭제합니다.")
	public CommonResponse<String> deleteGroup(@PathVariable("groupId") Long groupId) {
		groupCommandService.deleteGroup(groupId);

		return CommonResponse.onSuccess("그룹이 삭제되었습니다.");
	}

	// 그룹 검색 결과 가져오기
	@GetMapping("/search")
	@Operation(summary = "그룹 검색 결과 가져오기", description = "검색하신 그룹 검색 결과를 가져옵니다.")
	public CommonResponse<GroupResponseDto.SearchedGroupInfoList> searchGroups(
		@RequestParam String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by("groupName").ascending());

		return CommonResponse.onSuccess(groupQueryService.getSearchedGroupInfoList(keyword, pageRequest));
	}

	// 그룹 가입 요청
	@PostMapping("/{groupId}/join-request")
	@Operation(summary = "그룹 가입 요청", description = "해당 그룹에 가입을 요청하였습니다.")
	public CommonResponse<String> requestJoinGroup(@PathVariable("groupId") Long groupId) {
		groupCommandService.requestJoinGroup(groupId);

		return CommonResponse.onSuccess("그룹 가입 요청을 보냈습니다.");
	}

	// 그룹 가입 요청 리스트
	@GetMapping("/{groupId}/join-request")
	@Operation(summary = "그룹 가입 요청 리스트", description = "해당 그룹 가입 요청 리스트입니다.")
	public CommonResponse<List<GroupResponseDto.JoinRequestDTO>> findByGroupAndStatus(
		@PathVariable("groupId") Long groupId) {
		List<GroupResponseDto.JoinRequestDTO> joinRequests = groupQueryService.findByGroupAndStatus(groupId);

		return CommonResponse.onSuccess(joinRequests);
	}

	// 그룹 가입 요청 수락 및 그룹 가입
	@PostMapping("/{groupId}/accept-join/{requestId}")
	@Operation(summary = "그룹 가입 요청 수락 및 그룹 가입", description = "해당 맴버의 그룹 가입 요청을 수락하였습니다.")
	public CommonResponse<String> acceptJoinGroup(@PathVariable("groupId") Long groupId,
		@PathVariable("requestId") Long requestId) {
		groupCommandService.acceptJoinGroup(groupId, requestId);

		return CommonResponse.onSuccess("요청을 수락하였습니다.");
	}

	// 그룹 가입 요청 거절
	@PostMapping("/{groupId}/reject-join/{requestId}")
	@Operation(summary = "그룹 가입 요청 거절", description = "해당 맴버의 그룹 가입 요청을 거절하였습니다.")
	public CommonResponse<String> rejectJoinGroup(@PathVariable("groupId") Long groupId,
		@PathVariable("requestId") Long requestId) {
		groupCommandService.rejectJoinGroup(groupId, requestId);

		return CommonResponse.onSuccess("요청을 거절하였습니다.");
	}

	// 생성된 모든 그룹 리스트 가져오기
	@GetMapping("")
	@Operation(summary = "생성된 모든 그룹 리스트 가져오기", description = "현재 생성된 모든 그룹 리스트입니다.")
	public CommonResponse<Page<GroupResponseDto.GroupSummaryDTO>> getAllGroups(
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size) {
		Page<GroupResponseDto.GroupSummaryDTO> groups = groupQueryService.getAllGroups(page, size);

		return CommonResponse.onSuccess(groups);
	}

	// 핫한 그룹 기준
	@GetMapping("/popular")
	@Operation(summary = "핫한 그룹 기준", description = "인기가 많은 그룹 기준 리스트입니다.")
	public CommonResponse<Page<GroupResponseDto.GroupSummaryDTO>> getHotGroups(
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size) {
		Page<GroupResponseDto.GroupSummaryDTO> groups = groupQueryService.getHotGroups(page, size);

		return CommonResponse.onSuccess(groups);
	}

	// 일찍 일어나는 그룹 기준
	@GetMapping("/early")
	@Operation(summary = "일찍 일어나는 그룹 기준", description = "일찍 일어나는 그룹 기준 리스트입니다.")
	public CommonResponse<Page<GroupResponseDto.GroupSummaryDTO>> getEarlyMorningGroups(
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size) {

		Page<GroupResponseDto.GroupSummaryDTO> groups = groupQueryService.getEarlyMorningGroups(page, size);

		return CommonResponse.onSuccess(groups);
	}

	// 늦게 일어나는 그룹 기준
	@GetMapping("/late")
	@Operation(summary = "늦게 일어나는 그룹 기준", description = "늦게 일어나는 그룹 기준 리스트입니다.")
	public CommonResponse<Page<GroupResponseDto.GroupSummaryDTO>> getGroupsByLateEvening(
		@RequestParam(defaultValue = "0") Integer page,
		@RequestParam(defaultValue = "10") Integer size) {

		Page<GroupResponseDto.GroupSummaryDTO> groups = groupQueryService.getGroupsByLateEvening(page, size);

		return CommonResponse.onSuccess(groups);
	}

	// 리더 변경
	@PostMapping("/{groupId}/changeLeaderAuthority")
	@Operation(summary = "리더 변경", description = "해당 그룹의 리더를 변경합니다.")
	public CommonResponse<String> changeLeaderAuthority(@PathVariable("groupId") Long groupId,
		@RequestParam("newLeaderId") Long newLeaderId) {
		groupCommandService.changeLeaderAuthority(groupId, newLeaderId);

		return CommonResponse.onSuccess("반장 권한이 변경되었습니다.");

	}

}
