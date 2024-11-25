package com.ghpg.morningbuddies.domain.gamesession.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.gameroom.entity.GameRoom;
import com.ghpg.morningbuddies.domain.puzzlegame.entity.PuzzleGame;
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
import jakarta.persistence.ManyToOne;
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
@DynamicUpdate
@DynamicInsert
public class GameSession extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_session_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_room_id")
	private GameRoom gameRoom;

	@OneToOne(mappedBy = "gameSession", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	private PuzzleGame puzzleGame;

	private LocalDateTime startedAt;

	private LocalDateTime endedAt;

	@Enumerated(EnumType.STRING)
	private GameSessionStatus gameSessionStatus;

	@Enumerated(EnumType.STRING)
	private GameState gameState;
}
