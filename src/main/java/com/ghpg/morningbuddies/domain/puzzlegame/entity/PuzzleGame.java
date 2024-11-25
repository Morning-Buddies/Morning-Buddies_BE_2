package com.ghpg.morningbuddies.domain.puzzlegame.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.gamesession.entity.GameSession;
import com.ghpg.morningbuddies.domain.puzzlepiece.entity.PuzzlePiece;
import com.ghpg.morningbuddies.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@DynamicUpdate
@DynamicInsert
public class PuzzleGame extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "puzzle_game_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_session_id")
	private GameSession gameSession;

	@OneToMany(mappedBy = "puzzleGame", orphanRemoval = true, cascade = CascadeType.ALL)
	@Builder.Default
	private Set<PuzzlePiece> puzzlePieces = new HashSet<>();

	private String imageUrl;

	private Integer width;

	private Integer height;

	private Integer pieceCount;

}
