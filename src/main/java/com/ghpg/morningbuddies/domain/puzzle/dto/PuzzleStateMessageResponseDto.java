package com.ghpg.morningbuddies.domain.puzzle.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PuzzleStateMessageResponseDto {

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class PuzzleState {
		private Long puzzleId;
		private List<PieceState> pieceStates;
	}

	@Getter
	@Builder
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@AllArgsConstructor
	public static class PieceState {
		private Long pieceId;
		private Integer currentX;
		private Integer currentY;
		private boolean isPlaced;
		private String pieceImageUrl;
	}
}
