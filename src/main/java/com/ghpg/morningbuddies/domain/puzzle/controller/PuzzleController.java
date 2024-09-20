package com.ghpg.morningbuddies.domain.puzzle.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ghpg.morningbuddies.domain.puzzle.dto.PuzzleStateMessageResponseDto;
import com.ghpg.morningbuddies.domain.puzzle.service.OpenAIService;
import com.ghpg.morningbuddies.domain.puzzle.service.PuzzleCommandService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/puzzle")
public class PuzzleController {

	private final OpenAIService openAIService;
	private final PuzzleCommandService puzzleCommandService;

	@MessageMapping("/puzzle/start/{groupId}")
	@SendTo("/topic/puzzle/{groupId}")
	public PuzzleStateMessageResponseDto.PuzzleState startPuzzleGame(@DestinationVariable Long groupId) {
		return puzzleCommandService.startNewGame(groupId);
	}

}
