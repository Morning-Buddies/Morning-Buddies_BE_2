package com.ghpg.morningbuddies.domain.group.dto;

import com.ghpg.morningbuddies.auth.member.dto.MemberResponseDto;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import lombok.*;

import java.time.LocalTime;
import java.util.List;


public class GroupResponseDto {

    // 그룹 정보 DTO
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class GroupDetailDTO{
        private Long groupId;
        private String groupName;
        private String description;
        private LocalTime wakeUpTime;
        private int currentParticipantCount;
        private int maxParticipantCount;
        private String imageUrl;
        private List<MemberResponseDto.MemberSummaryDTO> members;
        private LeaderDTO leader;
    }

    // 리더 DTO
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class LeaderDTO{
        private Long id;
        private String firstName;
        private String lastName;
        private String email;

        public static LeaderDTO from(Member member){
            return LeaderDTO.builder()
                    .id(member.getId())
                    .firstName(member.getFirstName())
                    .lastName(member.getLastName())
                    .email(member.getEmail())
                    .build();
        }
    }
}
