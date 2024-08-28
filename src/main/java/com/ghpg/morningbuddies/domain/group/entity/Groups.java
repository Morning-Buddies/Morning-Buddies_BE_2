package com.ghpg.morningbuddies.domain.group.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.MemberGroup;
import com.ghpg.morningbuddies.domain.chatmessage.ChatMessage;
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

    @Lob
    private String groupImage;

    private boolean isActivated;

    private LocalTime timeOut;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // 순환 참조 방지
    private List<MemberGroup> memberGroups = new ArrayList<>();

    /*
     * 편의 메서드
     * */

    public void addMember(Member member) {
        MemberGroup memberGroup = MemberGroup.createMemberGroup(member, this);
        memberGroups.add(memberGroup);
        member.getMemberGroups().add(memberGroup);
    }

}
