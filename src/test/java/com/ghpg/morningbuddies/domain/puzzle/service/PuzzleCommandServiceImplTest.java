package com.ghpg.morningbuddies.domain.puzzle.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencv.core.Core;

import com.ghpg.morningbuddies.domain.game.Game;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.domain.puzzle.Puzzle;
import com.ghpg.morningbuddies.domain.puzzle.dto.PuzzleStateMessageResponseDto;
import com.ghpg.morningbuddies.domain.puzzle.repository.GameRepository;
import com.ghpg.morningbuddies.domain.puzzle.repository.PuzzlePieceRepository;
import com.ghpg.morningbuddies.domain.puzzle.repository.PuzzleRepository;
import com.ghpg.morningbuddies.global.aws.s3.S3Service;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.group.GroupException;

@ExtendWith(MockitoExtension.class)
public class PuzzleCommandServiceImplTest {

	@InjectMocks
	private PuzzleCommandServiceImpl puzzleCommandService;

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private GameRepository gameRepository;

	@Mock
	private S3Service s3Service;

	@Mock
	private OpenAIService openAIService;

	@Mock
	private PuzzlePieceRepository puzzlePieceRepository;

	@Mock
	private PuzzleRepository puzzleRepository;

	@BeforeEach
	public void setUp() {
		// OpenCV 네이티브 라이브러리 로드
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Mockito 초기화
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testStartNewGame_Success() throws Exception {
		// Given
		Long groupId = 1L;
		String groupName = "Test Group";
		String description = "Group Description";
		String prompt = groupName + " " + description;
		String imageUrl = "http://example.com/image.jpg";
		String imageUrlSavedOnS3 = "http://s3.amazonaws.com/bucket/image.jpg";
		String localImagePath = "temp/image.jpg";

		Groups group = Groups.builder()
			.id(groupId)
			.groupName(groupName)
			.description(description)
			.build();

		when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
		when(openAIService.generateImage(prompt)).thenReturn(imageUrl);
		when(s3Service.transferImageToS3(imageUrl)).thenReturn(imageUrlSavedOnS3);
		when(s3Service.downloadImageFromS3(imageUrlSavedOnS3)).thenReturn(localImagePath);

		Game savedGame = Game.builder()
			.id(1L)
			.name(groupName)
			.startedAt(LocalDateTime.now())
			.build();
		when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

		Puzzle savedPuzzle = Puzzle.builder()
			.id(1L)
			.width(4)
			.height(4)
			.pieceCount(16)
			.imageUrl(imageUrlSavedOnS3)
			.game(savedGame)
			.build();
		when(puzzleRepository.save(any(Puzzle.class))).thenReturn(savedPuzzle);

		// 퍼즐 조각 저장 시 null 반환 (필요에 따라 조정)
		when(puzzlePieceRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

		// getCurrentState 메서드 모킹 (실제 구현이 되어 있다면 필요 없음)
		// PuzzleStateMessageResponseDto.PuzzleState mockPuzzleState = ...;
		// when(puzzleCommandService.getCurrentState(savedPuzzle.getId())).thenReturn(mockPuzzleState);

		// When
		PuzzleStateMessageResponseDto.PuzzleState puzzleState = puzzleCommandService.startNewGame(groupId);

		// Then
		assertNotNull(puzzleState);
		assertEquals(savedPuzzle.getId(), puzzleState.getPuzzleId());
		assertNotNull(puzzleState.getPieceStates());
		assertEquals(16, puzzleState.getPieceStates().size());

		// 상호작용 검증
		verify(groupRepository).findById(groupId);
		verify(openAIService).generateImage(prompt);
		verify(s3Service).transferImageToS3(imageUrl);
		verify(s3Service).downloadImageFromS3(imageUrlSavedOnS3);
		verify(gameRepository).save(any(Game.class));
		verify(puzzleRepository).save(any(Puzzle.class));
		verify(puzzlePieceRepository).saveAll(anyList());
	}

	@Test
	public void testStartNewGame_GroupNotFound() {
		// Given
		Long groupId = 1L;
		when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

		// When & Then
		GroupException exception = assertThrows(GroupException.class, () -> {
			puzzleCommandService.startNewGame(groupId);
		});

		assertEquals(GlobalErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());

		verify(groupRepository).findById(groupId);
		verifyNoMoreInteractions(openAIService, s3Service, gameRepository, puzzleRepository, puzzlePieceRepository);
	}

	// 추가적인 예외 상황에 대한 테스트를 작성하세요 (예: OpenAIService 실패, S3Service 실패 등)
}
