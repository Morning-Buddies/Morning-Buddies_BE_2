package com.ghpg.morningbuddies.domain.group.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalTime;

public class GroupRequestDto {

    @Getter
    public static class CreateGroupDto{

        @NotNull
        private Integer groupId;

        @NotBlank
        private String groupName;

        @NotNull
        private LocalTime wakeUpTime;

        @Min(value = 1)
        @Max(value = 10)
        @NotNull
        private Integer maxParticipantCount = 1;

        @NotBlank
        @Size(max = 500)
        private String description;

        @Lob
        private String imageUrl;
    }
}
