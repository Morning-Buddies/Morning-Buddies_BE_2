package com.ghpg.morningbuddies.domain.group.service;

import com.ghpg.morningbuddies.domain.group.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface GroupCommandService {

    // 그룹 생성
    GroupResponseDto.GroupDetailDTO createGroup(GroupRequestDto.CreateGroupDto requestDto, MultipartFile file);
}
