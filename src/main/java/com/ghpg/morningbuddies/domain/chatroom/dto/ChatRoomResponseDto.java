package com.ghpg.morningbuddies.domain.chatroom.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomResponseDto {

    private Long id;
    private String groupName;

}
