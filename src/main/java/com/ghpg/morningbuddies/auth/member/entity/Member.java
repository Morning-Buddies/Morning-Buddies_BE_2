package com.ghpg.morningbuddies.auth.member.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ghpg.morningbuddies.domain.allowance.MemberAllowance;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.recommend.Recommend;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	private String fcmToken;

	private String email;

	private String password;

	private String firstName;

	private String lastName;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@ColumnDefault("0")
	private Integer age;

	@Lob
	private byte[] profileImage;

	LocalTime preferredWakeupTime;

	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	private String phoneNumber;

	private boolean isActivated;

	private UserRole userRole;

	@Builder.Default
	@OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Groups> groups = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MemberAllowance> memberAllowances = new ArrayList<>();

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Recommend recommend;

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();

	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private RefreshToken refreshToken;

	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@JsonManagedReference // 순환 참조 방지
	private List<MemberGroup> memberGroups = new ArrayList<>();

	/*
	 * 사용자 편의 메서드
	 * */
	public void changePassword(String password) {
		this.password = password;
	}

	public void registerFcmToken(String fcmToken, String deviceId) {
		this.fcmToken = fcmToken;
	}
}
