package com.ghpg.morningbuddies.auth.member.dto;

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

        private List<GroupInfo> groups;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class GroupInfo {
        private String name;

        private LocalTime wakeupTime;
    }
}
