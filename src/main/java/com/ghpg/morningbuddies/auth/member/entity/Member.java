package com.ghpg.morningbuddies.auth.member.entity;

import com.ghpg.morningbuddies.domain.ChatMessage;
import com.ghpg.morningbuddies.domain.allowance.MemberAllowance;
import com.ghpg.morningbuddies.domain.recommend.Recommend;
import com.ghpg.morningbuddies.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalTime;:
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

    private String email;

    private String password;

    private String name;

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
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

}
