package com.ghpg.morningbuddies.global.exception.common.code;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.ghpg.morningbuddies.global.exception.common.ErrorReason;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
	// Global (GLOBAL)
	_INTERNAL_SERVER_ERROR(INTERNAL_SERVER_ERROR, "GLOBAL_500_0", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(BAD_REQUEST, "GLOBAL_400_0", "잘못된 요청입니다."),
	_VALIDATION_ERROR(BAD_REQUEST, "GLOBAL_400_1", "요청 데이터 검증에 실패했습니다."),

	// Authentication & Authorization (AUTH)
	LOGOUT_FAILED(BAD_REQUEST, "AUTH_400_0", "잘못된 로그아웃 요청입니다."),
	TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_0", "인증 토큰이 만료되었습니다."),
	INVALID_TOKEN(UNAUTHORIZED, "AUTH_401_1", "유효하지 않은 토큰입니다."),
	REFRESH_TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_2", "리프레시 토큰이 만료되었습니다."),
	ACCESS_TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_3", "액세스 토큰이 만료되었습니다."),
	LOGIN_REQUIRED(UNAUTHORIZED, "AUTH_401_4", "로그인이 필요한 서비스입니다."),
	INVALID_CREDENTIALS(UNAUTHORIZED, "AUTH_401_5", "로그인 정보가 올바르지 않습니다."),
	AUTHENTICATION_DENIED(FORBIDDEN, "AUTH_403_0", "접근 권한이 없습니다."),
	LOGIN_FAILED(INTERNAL_SERVER_ERROR, "AUTH_500_0", "로그인에 실패했습니다."),
	AUTHENTICATION_REQUIRED(INTERNAL_SERVER_ERROR, "AUTH_500_1", "인증 정보가 필요합니다."),

	// Member (MEMBER)
	MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_404_0", "회원을 찾을 수 없습니다."),
	MEMBER_ALREADY_EXISTS(CONFLICT, "MEMBER_409_0", "이미 존재하는 회원입니다."),

	// Group (GROUP)
	GROUP_NOT_FOUND(NOT_FOUND, "GROUP_404_0", "그룹을 찾을 수 없습니다."),
	REQUEST_NOT_FOUND(NOT_FOUND, "GROUP_404_1", "그룹 가입 요청을 찾을 수 없습니다."),
	GROUP_ALREADY_EXISTS(CONFLICT, "GROUP_409_0", "그룹이 이미 존재합니다."),
	GROUP_FULL(CONFLICT, "GROUP_409_1", "그룹 인원이 가득 찼습니다."),
	GROUP_PERMISSION_DENIED(FORBIDDEN, "GROUP_403_0", "그룹에 대한 권한이 없습니다."),
	LEADER_CANNOT_LEAVE(FORBIDDEN, "GROUP_403_1", "그룹장은 탈퇴할 수 없습니다."),
	MEMBER_NOT_IN_GROUP(FORBIDDEN, "GROUP_403_2", "그룹에 속해있지 않은 회원입니다."),
	MEMBER_ALREADY_JOINED(CONFLICT, "GROUP_409_2", "이미 가입한 그룹입니다."),

	// ChatRoom (CHAT)
	CHATROOM_NOT_FOUND(NOT_FOUND, "CHAT_404_0", "채팅방을 찾을 수 없습니다."),
	MEMBER_NOT_IN_CHATROOM(FORBIDDEN, "CHAT_403_0", "채팅방에 속해있지 않은 회원입니다."),

	// File (FILE)
	FILE_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "FILE_500_0", "파일 업로드에 실패했습니다."),
	FILE_DOWNLOAD_FAILED(INTERNAL_SERVER_ERROR, "FILE_500_1", "파일 다운로드에 실패했습니다."),

	// Puzzle (PUZZLE)
	PUZZLE_NOT_FOUND(NOT_FOUND, "PUZZLE_404_0", "퍼즐을 찾을 수 없습니다."),
	PUZZLE_SAVE_FAILED(INTERNAL_SERVER_ERROR, "PUZZLE_500_0", "퍼즐 저장에 실패했습니다."),

	// Game (GAME)
	GAME_NOT_FOUND(NOT_FOUND, "GAME_404_0", "게임을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReason getReason() {
		return ErrorReason.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReason getReasonHttpStatus() {
		return ErrorReason.builder()
			.message(message)
			.code(code)
			.httpStatus(httpStatus)
			.isSuccess(false)
			.build();
	}
}