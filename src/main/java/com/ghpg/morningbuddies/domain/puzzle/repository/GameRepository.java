package com.ghpg.morningbuddies.domain.puzzle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ghpg.morningbuddies.domain.game.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
