package com.ghpg.morningbuddies.auth.member.entity;

import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberGroup extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_group_id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "group_id")
    private Groups group;

    /*
     * 편의 메서드
     * */

    public static MemberGroup createMemberGroup(Member member, Groups group) {
        MemberGroup memberGroup = MemberGroup.builder()
                .member(member)
                .group(group)
                .build();

        member.getMemberGroups().add(memberGroup);
        group.getMemberGroups().add(memberGroup);

        return memberGroup;
    }

}
