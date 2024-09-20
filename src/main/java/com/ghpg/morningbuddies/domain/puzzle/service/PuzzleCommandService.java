package com.ghpg.morningbuddies.domain.puzzle.service;

import com.ghpg.morningbuddies.domain.puzzle.dto.PuzzleStateMessageResponseDto;

public interface PuzzleCommandService {
	PuzzleStateMessageResponseDto.PuzzleState startNewGame(Long groupId);
}
