package com.ghpg.morningbuddies.domain.puzzle.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.puzzle.PuzzlePiece;

public interface PuzzlePieceRepository extends JpaRepository<PuzzlePiece, Long> {
	List<PuzzlePiece> findByPuzzleId(Long puzzleId);
}
