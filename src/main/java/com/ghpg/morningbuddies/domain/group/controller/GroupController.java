package com.ghpg.morningbuddies.domain.group.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import com.ghpg.morningbuddies.domain.group.service.GroupCommandService;
import com.ghpg.morningbuddies.domain.group.service.GroupQueryService;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/groups")
public class GroupController {

    private final GroupCommandService groupCommandService;
    private final GroupQueryService groupQueryService;
    private final ObjectMapper objectMapper;

    // 그룹 생성
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResponse<GroupResponseDto.GroupDetailDTO> createGroup(@RequestPart("request") String requestJson, @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        GroupRequestDto.CreateGroupDto request = objectMapper.readValue(requestJson, GroupRequestDto.CreateGroupDto.class);
        GroupResponseDto.GroupDetailDTO group = groupCommandService.createGroup(request, file);

        return CommonResponse.onSuccess(group);
    }

    // 그룹 정보 가져오기
    @GetMapping("/{groupId}")
    public CommonResponse<GroupResponseDto.GroupDetailDTO> getGroupDetailsById(@PathVariable("groupId") Long groupId){
        GroupResponseDto.GroupDetailDTO group = groupQueryService.getGroupDetailById(groupId);

        return CommonResponse.onSuccess(group);
    }
}
