package com.ghpg.morningbuddies.domain.memberchatroom.entity;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MemberChatRoom extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_chat_room_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "chatroom_id", nullable = false)
	private ChatRoom chatRoom;

	/*
	 * 연관 관계 편의 메서드
	 * */

	public static MemberChatRoom createMemberChatRoom(Member member, ChatRoom chatRoom) {
		MemberChatRoom newMemberChatRoom = MemberChatRoom.builder().build();

		newMemberChatRoom.setMember(member);
		newMemberChatRoom.setChatRoom(chatRoom);

		return newMemberChatRoom;
	}

	public void setMember(Member member) {
		if (this.member != null) {
			this.member.getMemberChatRooms().remove(this);
		}

		this.member = member;
		member.getMemberChatRooms().add(this);
	}

	public void setChatRoom(ChatRoom chatRoom) {

		if (this.chatRoom != null) {
			this.chatRoom.getMemberChatRooms().remove(this);
		}

		this.chatRoom = chatRoom;
		chatRoom.getMemberChatRooms().add(this);

	}

}
