package com.ghpg.morningbuddies.domain.allowance;

import com.ghpg.morningbuddies.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
public class Allowance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_id")
    private Long id;

    private String title;

    @Lob
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "allowance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberAllowance> memberAllowances = new ArrayList<>();

}
