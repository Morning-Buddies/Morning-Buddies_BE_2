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

	/*
	 * 편의 메서드
	 * */

	public static MemberGroup createMemberGroup(Member member) {
		MemberGroup memberGroup = MemberGroup.builder().build();
		memberGroup.setMember(member);
		return memberGroup;
	}

	public void setMember(Member member) {
		this.member = member;
		member.getMemberGroups().add(this);
		member.setCurrentGroupCount(member.getCurrentGroupCount() + 1);
	}

	public void setGroup(Groups group) {
		this.group = group;
	}

	public boolean isLeader() {
		return group.getLeader().getId().equals(member.getId());
	}

	public void remove() {
		if (this.group != null) {
			this.member.decreaseGroupCount();
			this.member = null;
			this.group = null;
		}
	}

}
