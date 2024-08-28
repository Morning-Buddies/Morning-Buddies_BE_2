package com.ghpg.morningbuddies.auth.member.dto;

import com.ghpg.morningbuddies.domain.group.dto.GroupResponseDto;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

public class MemberResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class MemberInfo {
        private byte[] profileImage;

        private String firstName;

        private String lastName;

        private LocalTime preferredWakeupTime;

        private Integer successGameCount;

        private List<GroupResponseDto.GroupInfo> groups;
    }


}
