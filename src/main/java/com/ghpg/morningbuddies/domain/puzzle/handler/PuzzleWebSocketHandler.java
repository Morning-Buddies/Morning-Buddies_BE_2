package com.ghpg.morningbuddies.domain.puzzle.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ghpg.morningbuddies.domain.puzzle.dto.PuzzleStateMessageResponseDto;
import com.ghpg.morningbuddies.domain.puzzle.service.OpenAIService;
import com.ghpg.morningbuddies.domain.puzzle.service.PuzzleCommandService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PuzzleWebSocketHandler extends TextWebSocketHandler {

	private final OpenAIService openAIService;
	private final PuzzleCommandService puzzleCommandService;
	private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// Extract groupId from the URI
		Long groupId = getGroupId(session);
		sessions.put(groupId, session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Long groupId = getGroupId(session);
		String payload = message.getPayload();

		// Assuming the client sends a specific message to start the game
		if ("start".equalsIgnoreCase(payload)) {
			PuzzleStateMessageResponseDto.PuzzleState puzzleState = puzzleCommandService.startNewGame(groupId);
			String response = convertPuzzleStateToJson(puzzleState);
			session.sendMessage(new TextMessage(response));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long groupId = getGroupId(session);
		sessions.remove(groupId);
	}

	private Long getGroupId(WebSocketSession session) {
		String uri = session.getUri().toString();
		// Extract groupId from the URI, e.g., /puzzle/start/{groupId}
		// You might need to parse the URI to get the groupId
		// For simplicity, let's assume groupId is the last segment
		String[] parts = uri.split("/");
		return Long.valueOf(parts[parts.length - 1]);
	}

	private String convertPuzzleStateToJson(PuzzleStateMessageResponseDto.PuzzleState puzzleState) {
		// Use your preferred JSON library to convert the puzzleState to JSON
		// For example, using Jackson ObjectMapper:
		// ObjectMapper mapper = new ObjectMapper();
		// return mapper.writeValueAsString(puzzleState);
		return ""; // Replace with actual implementation
	}
}