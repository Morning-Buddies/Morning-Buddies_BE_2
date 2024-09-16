package com.ghpg.morningbuddies.domain.game.puzzle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.game.puzzle.PuzzlePiece;

public interface PuzzlePieceRepository extends JpaRepository<PuzzlePiece, Long> {
}
