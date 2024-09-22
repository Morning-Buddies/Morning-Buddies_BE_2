package com.ghpg.morningbuddies.domain.chatroom.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatRoomRequestDto {

    private Long chatRoomId;
    private String chatRoomName;

}
