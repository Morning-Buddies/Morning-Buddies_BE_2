package com.ghpg.morningbuddies.domain.chatroom.dto;

import lombok.*;

public class ChatRoomResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class AllChatRoomByMemberId {
        private Long id;
        private String groupName;
    }
}
