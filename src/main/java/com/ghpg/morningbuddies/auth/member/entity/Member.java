package com.ghpg.morningbuddies.auth.member.entity;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.ghpg.morningbuddies.auth.member.dto.MemberRequestDto;
import com.ghpg.morningbuddies.auth.member.entity.enums.Gender;
import com.ghpg.morningbuddies.auth.member.entity.enums.SocialType;
import com.ghpg.morningbuddies.auth.member.entity.enums.UserRole;
import com.ghpg.morningbuddies.auth.refreshtoken.entity.RefreshToken;
import com.ghpg.morningbuddies.domain.allowance.MemberAllowance;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.groups.entity.Groups;
import com.ghpg.morningbuddies.domain.memberchatroom.entity.MemberChatRoom;
import com.ghpg.morningbuddies.domain.membergroup.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.notification.Notification;
import com.ghpg.morningbuddies.domain.recommend.Recommend;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private String deviceId;

	private String email;

	private String password;

	private String firstName;

	private String lastName;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@ColumnDefault("0")
	private Integer age;

	private String profileImageUrl;

	LocalTime preferredWakeupTime;

	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	private String phoneNumber;

	@Builder.Default
	private Boolean isDeleted = false;

	@Builder.Default
	private Integer successGameCount = 0;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Builder.Default
	private Integer currentGroupCount = 0;

	@Builder.Default
	private Integer maxGroupCount = 2;

	@OneToMany(mappedBy = "leader", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Groups> groups = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MemberAllowance> memberAllowances = new ArrayList<>();

	@OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Recommend recommend;

	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ChatMessage> chatMessages = new ArrayList<>();

	@OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private RefreshToken refreshToken;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@Builder.Default
	private List<MemberGroup> memberGroups = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Notification> notifications = new ArrayList<>();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MemberChatRoom> memberChatRooms = new ArrayList<>();

	/*
	 * 사용자 편의 메서드
	 * */

	public static Member createMember(MemberRequestDto.JoinDto request, BCryptPasswordEncoder bCryptPasswordEncoder) {
		return Member.builder()
			.email(request.getEmail())
			.password(bCryptPasswordEncoder.encode(request.getPassword()))
			.firstName(request.getFirstName())
			.lastName(request.getLastName())
			.preferredWakeupTime(request.getPreferredWakeupTime())
			.phoneNumber(request.getPhoneNumber())
			.role(UserRole.ROLE_USER)
			.build();
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void updateFcmToken(String fcmToken, String deviceId) {
		this.fcmToken = fcmToken;
		this.deviceId = deviceId;
	}

	public void setRefreshToken(RefreshToken refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void delete() {
		this.isDeleted = true;
	}

}
