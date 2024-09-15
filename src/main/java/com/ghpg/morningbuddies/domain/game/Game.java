package com.ghpg.morningbuddies.domain.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.GameType;
import com.ghpg.morningbuddies.domain.game.gameparticipant.GameParticipant;
import com.ghpg.morningbuddies.domain.game.puzzle.Puzzle;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@DynamicInsert
@DynamicUpdate
public class Game extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Long id;

	@OneToOne(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
	private Puzzle puzzle;

	@Builder.Default
	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GameParticipant> gameParticipants = new ArrayList<>();

	private String name;

	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Enumerated(EnumType.STRING)
	private GameType gameType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Groups group;
}
