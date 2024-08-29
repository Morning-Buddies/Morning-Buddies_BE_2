package com.ghpg.morningbuddies.domain.group.service;

import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;

public interface GroupQueryService {

    // 그룹 정보 가져오기
    GroupResponseDto.GroupDetailDTO getGroupDetailById(Long groupId);
}
