package com.ghpg.morningbuddies.domain.puzzle;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.ghpg.morningbuddies.domain.game.Game;
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
public class Puzzle extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "puzzle_id")
	private Long id;

	@Builder.Default
	@OneToMany(mappedBy = "puzzle", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PuzzlePiece> puzzlePieces = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	private String imageUrl;

	@ColumnDefault("0")
	private Integer width;

	@ColumnDefault("0")
	private Integer height;

	@ColumnDefault("0")
	private Integer pieceCount;

	/*
	 * 사용자 편의 메서드
	 * */

	public void setPuzzlePieces(List<PuzzlePiece> puzzlePieces) {
		this.puzzlePieces.clear();
		this.puzzlePieces.addAll(puzzlePieces);
	}
}
