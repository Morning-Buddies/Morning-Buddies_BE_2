package com.ghpg.morningbuddies.domain.chatroom.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class ChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chatroom_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Groups group;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ChatMessage> message = new ArrayList<>();

	/*
	 * 편의 메서드
	 * */

	public static ChatRoom createChatRoom(Groups group) {
		ChatRoom chatRoom = ChatRoom.builder().build();
		chatRoom.setGroup(group);
		return chatRoom;
	}

	// 연관관계 편의 메서드
	public void setGroup(Groups group) {
		// 기존 관계 제거
		if (this.group != null) {
			this.group.setChatRoom(null);
		}

		this.group = group;

		// 무한루프 방지
		if (group != null && group.getChatRoom() != this) {
			group.setChatRoom(this);
		}
	}

	public void addMemberChatRoom(MemberChatRoom memberChatRoom) {
		this.memberChatRooms.add(memberChatRoom);
		memberChatRoom.setChatRoom(this);
	}

}
