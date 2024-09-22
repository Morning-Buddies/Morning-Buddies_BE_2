package com.ghpg.morningbuddies.domain.chatroom;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberChatRoom;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@Table(name="chat_room")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @OneToOne
    @JoinColumn(name="group_id", nullable = false)
    private Groups group;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberChatRoom> members = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> message; // 채팅방의 메세지들

    public ChatRoom(Groups group) {
        this.group = group;
    }
}
