package com.ghpg.morningbuddies.auth.member.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
import com.ghpg.morningbuddies.domain.allowance.MemberAllowance;
import com.ghpg.morningbuddies.domain.recommend.Recommend;
import com.ghpg.morningbuddies.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


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

}
