package com.ghpg.morningbuddies.domain.group.service;

import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface GroupCommandService {

    // 그룹 생성
    GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.CreateGroupDto requestDto, MultipartFile file);

    // 그룹 수정
    GroupResponseDto.GroupDetailDTO updateGroup(Long groupId, GroupRequestDto.UpdateGroupDTO request, MultipartFile file);

    // 그룹 삭제
    void deleteGroup(Long groupId);
}
