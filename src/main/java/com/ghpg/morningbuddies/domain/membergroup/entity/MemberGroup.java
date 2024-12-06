package com.ghpg.morningbuddies.domain.membergroup.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.global.common.BaseEntity;

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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class MemberGroup extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_group_id")
	private Long id; // PK

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Groups group;

	private Boolean isLeader; //

	//==생성 메서드==//
	public static MemberGroup createMemberGroup(Member member, Groups group, Boolean isLeader) {
		MemberGroup newMemberGroup = MemberGroup.builder()
			.isLeader(isLeader)
			.build();

		// 연관 관계 설정
		newMemberGroup.setMember(member);
		newMemberGroup.setGroup(group);

		// 멤버가 참가한 그룹 수 증가
		member.increaseCurrentGroupCount();

		// 그룹 현재 참가자 수 증가
		group.increaseCurrentParticipantsCount();

		return newMemberGroup;

	}

	public void setMember(Member member) {

		if (this.member != null) {
			this.member.getMemberGroups().remove(this);
		}

		this.member = member;
		member.getMemberGroups().add(this);

	}

	public void setIsLeader(Boolean isLeader) {
		this.isLeader = isLeader;
	}

	public void setGroup(Groups group) {
		if (this.group != null) {
			this.group.getMemberGroups().remove(this);
		}

		this.group = group;
		group.getMemberGroups().add(this);
	}

	public boolean isLeader() {
		return this.isLeader;
	}

	public void remove() {
		if (this.group != null) {
			this.member.decreaseGroupCount();
			this.member = null;
			this.group = null;
		}
	}

}
