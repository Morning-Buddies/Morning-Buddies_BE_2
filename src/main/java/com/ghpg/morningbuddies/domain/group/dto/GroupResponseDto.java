package com.ghpg.morningbuddies.domain.group.dto;

import lombok.*;

import java.time.LocalTime;

public class GroupResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class GroupInfo {
        private String name;

        private LocalTime wakeupTime;
    }
}
