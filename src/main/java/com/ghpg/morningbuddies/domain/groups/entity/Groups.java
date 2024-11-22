package com.ghpg.morningbuddies.domain.groups.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.chatroom.entity.ChatRoom;
import com.ghpg.morningbuddies.domain.groups.dto.GroupRequestDto;
import com.ghpg.morningbuddies.domain.groups.entity.enums.AlarmSound;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.notification.Notification;
import com.ghpg.morningbuddies.global.common.BaseEntity;
import com.ghpg.morningbuddies.global.exception.common.code.ErrorStatus;
import com.ghpg.morningbuddies.global.exception.group.GroupException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "`group`")
public class Groups extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id")
	private Long id;

	private String groupName;

	@Lob
	private String description;

	private LocalTime wakeupTime;

	@ColumnDefault("0")
	private Integer successCount;

	private String groupImageUrl;

	@ColumnDefault("true")
	private Boolean isActivated;

	@Column(nullable = false)
	@Builder.Default
	private LocalTime timeOut = LocalTime.of(0, 5);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leader_id", nullable = false) // 방장은 무조건 있어야 하므로 nullable = false
	private Member leader; // 그룹장

	@Builder.Default
	@ColumnDefault("0")
	private Integer currentParticipantCount = 0;

	@Builder.Default
	@ColumnDefault("0")
	private Integer maxParticipantCount = 0;

	@Enumerated(EnumType.STRING)
	private AlarmSound alarmSound;

	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MemberGroup> memberGroups = new ArrayList<>();

	@OneToMany(mappedBy = "groups", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Notification> notifications = new ArrayList<>();

	@OneToOne(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ChatRoom chatRoom; // 채팅방

	/*
	 * 연관 관계
	 * 편의 메서드
	 * */

	public static Groups createGroup(
		GroupRequestDto.GroupCommand createGroupDto,
		String groupImageUrl,
		Member leader,
		MemberGroup... memberGroups
	) {
		Groups group = Groups.builder()
			.groupName(createGroupDto.getGroupName())
			.wakeupTime(createGroupDto.getWakeUpTime())
			.maxParticipantCount(createGroupDto.getMaxParticipantCount())
			.description(createGroupDto.getDescription())
			.groupImageUrl(groupImageUrl)
			.currentParticipantCount(1)
			.leader(leader)
			.isActivated(true)
			.build();

		for (MemberGroup memberGroup : memberGroups) {
			group.addMemberGroup(memberGroup);
		}

		return group;

	}

	public void addMemberGroup(MemberGroup memberGroup) {
		if (this.currentParticipantCount >= this.maxParticipantCount) {
			throw new GroupException(ErrorStatus.GROUP_FULL);
		}

		if (this.memberGroups.contains(memberGroup)) {
			throw new GroupException(ErrorStatus.MEMBER_ALREADY_JOINED);
		}

		this.memberGroups.add(memberGroup);
		memberGroup.setGroup(this);
		this.currentParticipantCount++;
	}

	// 연관관계 편의 메서드
	public void setChatRoom(ChatRoom chatRoom) {
		// 기존 관계 제거
		if (this.chatRoom != null) {
			this.chatRoom.setGroup(null);
		}

		this.chatRoom = chatRoom;

		// 무한루프 방지
		if (chatRoom != null && chatRoom.getGroup() != this) {
			chatRoom.setGroup(this);
		}
	}

	public void removeMember(Member member) {
		MemberGroup memberGroup = this.memberGroups.stream()
			.filter(mg -> mg.getMember().equals(member))
			.findFirst()
			.orElseThrow(() -> new GroupException(ErrorStatus.MEMBER_NOT_IN_GROUP));

		this.memberGroups.remove(memberGroup);
		memberGroup.setGroup(null);
		this.currentParticipantCount--;
	}

	public boolean isMemberInGroup(Member member) {
		return this.memberGroups.stream().anyMatch(mg -> mg.getMember().equals(member));
	}


	public void updateGroup(GroupRequestDto.GroupCommand request,
		String groupImageUrl) {

		groupName = request.getGroupName();
		wakeupTime = request.getWakeUpTime();
		maxParticipantCount = request.getMaxParticipantCount();
		description = request.getDescription();
		this.groupImageUrl = groupImageUrl;

	}

	public boolean isLeader(Member member) {
		return this.leader.equals(member);
	}


}
