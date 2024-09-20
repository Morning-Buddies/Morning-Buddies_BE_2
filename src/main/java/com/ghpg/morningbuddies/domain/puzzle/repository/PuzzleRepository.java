package com.ghpg.morningbuddies.domain.puzzle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.puzzle.Puzzle;

public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {

}
