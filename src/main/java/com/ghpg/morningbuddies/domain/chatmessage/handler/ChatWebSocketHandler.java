package com.ghpg.morningbuddies.domain.chatmessage.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageRequestDto;
import com.ghpg.morningbuddies.domain.chatmessage.dto.ChatMessageResponseDto;
import com.ghpg.morningbuddies.domain.chatmessage.service.ChatMessageCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

	private final ChatMessageCommandService chatMessageCommandService;

	private final MemberRepository memberRepository;

	private final ObjectMapper objectMapper;

	// 세션과 그룹 ID를 매핑하기 위한 맵
	private final Map<WebSocketSession, Long> sessionGroupMap = new ConcurrentHashMap<>();

	// 그룹 ID와 해당 그룹의 세션들을 매핑하기 위한 맵
	private final Map<Long, Map<WebSocketSession, Long>> chatRoomSessions = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// URI에서 groupId와 memberId를 추출
		Long groupId = getChatRoomId(session);
		Long memberId = getMemberId(session);

		// 세션 저장
		sessionGroupMap.put(session, groupId);

		chatRoomSessions.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>()).put(session, memberId);

		// 필요한 경우 사용자 추가 로직 처리
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Long chatRoomId = sessionGroupMap.get(session);
		Long memberId = getMemberId(session);
		String payload = message.getPayload();

		// 수신된 메시지 파싱
		ClientMessage clientMessage = parseMessage(payload, ClientMessage.class);

		String action = clientMessage.getAction();

		if ("sendMessage".equalsIgnoreCase(action)) {
			ChatMessageRequestDto.Message chatMessage = clientMessage.getMessage();
			ChatMessageResponseDto.Message responseMessage = chatMessageCommandService.saveAndConvert(memberId,
				chatRoomId,
				chatMessage);
			String response = convertToJson(responseMessage);
			broadcastToChatRoom(chatRoomId, response);
		} else if ("addUser".equalsIgnoreCase(action)) {
			ChatMessageRequestDto.Message chatMessage = clientMessage.getMessage();
			ChatMessageResponseDto.Message responseMessage = chatMessageCommandService.addUserToGroup(memberId,
				chatRoomId,
				chatMessage);
			String response = convertToJson(responseMessage);
			broadcastToChatRoom(chatRoomId, response);
		} else if ("removeUser".equalsIgnoreCase(action)) {
			ChatMessageRequestDto.Message chatMessage = clientMessage.getMessage();
			ChatMessageResponseDto.Message responseMessage = chatMessageCommandService.removeUserFromGroup(memberId,
				chatRoomId, chatMessage);
			String response = convertToJson(responseMessage);
			broadcastToChatRoom(chatRoomId, response);
		} else {
			log.warn("Unknown action: {}", action);
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		Long groupId = sessionGroupMap.remove(session);
		if (groupId != null) {
			Map<WebSocketSession, Long> sessions = chatRoomSessions.get(groupId);
			if (sessions != null) {
				sessions.remove(session);
				if (sessions.isEmpty()) {
					chatRoomSessions.remove(groupId);
				}
			}
		}
		// 필요한 경우 사용자 제거 로직 처리
	}

	private void broadcastToChatRoom(Long chatRoomId, String message) {
		Map<WebSocketSession, Long> sessions = chatRoomSessions.get(chatRoomId);
		if (sessions != null) {
			sessions.keySet().forEach(session -> {
				try {
					session.sendMessage(new TextMessage(message));
				} catch (Exception e) {
					log.error("Error sending message", e);
				}
			});
		}
	}

	private Long getChatRoomId(WebSocketSession session) {
		String uri = session.getUri().toString();
		// URI에서 groupId 추출, 예: /chat/{chatRoomId}/{memberId}
		String[] parts = uri.split("/");
		// chatRoomId는 배열의 길이에서 두 번째 마지막 요소라고 가정
		return Long.valueOf(parts[parts.length - 2]);
	}

	private Long getMemberId(WebSocketSession session) {
		String uri = session.getUri().toString();
		// URI에서 memberId 추출, 예: /chat/{chatRoomId}/{memberId}
		String[] parts = uri.split("/");
		// memberId는 배열의 길이에서 마지막 요소라고 가정
		return Long.valueOf(parts[parts.length - 1]);
	}

	private String convertToJson(Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	private <T> T parseMessage(String message, Class<T> valueType) throws Exception {
		return objectMapper.readValue(message, valueType);
	}

	// 클라이언트 메시지 구조를 표현하는 내부 클래스
	private static class ClientMessage {
		private String action;
		private ChatMessageRequestDto.Message message;

		// Getter와 Setter

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public ChatMessageRequestDto.Message getMessage() {
			return message;
		}

		public void setMessage(ChatMessageRequestDto.Message message) {
			this.message = message;
		}
	}
}
