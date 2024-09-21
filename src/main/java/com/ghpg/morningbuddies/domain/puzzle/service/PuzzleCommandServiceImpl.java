package com.ghpg.morningbuddies.domain.puzzle.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.domain.game.Game;
import com.ghpg.morningbuddies.domain.group.entity.Groups;
import com.ghpg.morningbuddies.domain.group.repository.GroupRepository;
import com.ghpg.morningbuddies.domain.puzzle.Puzzle;
import com.ghpg.morningbuddies.domain.puzzle.PuzzlePiece;
import com.ghpg.morningbuddies.domain.puzzle.dto.PuzzleStateMessageResponseDto;
import com.ghpg.morningbuddies.domain.puzzle.repository.GameRepository;
import com.ghpg.morningbuddies.domain.puzzle.repository.PuzzlePieceRepository;
import com.ghpg.morningbuddies.domain.puzzle.repository.PuzzleRepository;
import com.ghpg.morningbuddies.global.aws.s3.S3Service;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.file.FileException;
import com.ghpg.morningbuddies.global.exception.group.GroupException;
import com.ghpg.morningbuddies.global.exception.puzzle.PuzzleException;
import com.ghpg.morningbuddies.global.util.MockMultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PuzzleCommandServiceImpl implements PuzzleCommandService {

	private final GroupRepository groupRepository;
	private final GameRepository gameRepository;

	private final S3Service s3Service;

	private final OpenAIService openAIService;
	private final PuzzlePieceRepository puzzlePieceRepository;
	private final PuzzleRepository puzzleRepository;

	@Override
	public PuzzleStateMessageResponseDto.PuzzleState startNewGame(Long groupId) {
		Groups currentGroup = groupRepository.findById(groupId).orElseThrow(
			() -> new GroupException(GlobalErrorCode.GROUP_NOT_FOUND));

		Game createdGame = Game.builder()
			.name(currentGroup.getGroupName())
			.startedAt(LocalDateTime.now())
			.build();

		createdGame = gameRepository.save(createdGame);

		// 그룹 이름과 설명을 합쳐서 prompt로 사용
		String groupName = currentGroup.getGroupName();
		String description = currentGroup.getDescription();

		String prompt = groupName + " " + description;

		// 이미지 생성
		String imageUrl = openAIService.generateImage(prompt);

		// S3에 이미지 업로드
		String imageUrlSavedOnS3 = s3Service.transferImageToS3(imageUrl);

		Puzzle createdPuzzle = Puzzle.builder()
			.width(4)
			.height(4)
			.pieceCount(16)
			.imageUrl(imageUrlSavedOnS3)
			.game(createdGame)
			.build();

		createdPuzzle = puzzleRepository.save(createdPuzzle);

		// S3에서 이미지 다운로드한 후에 로컬에 저장
		String localImagePath = s3Service.downloadImageFromS3(imageUrlSavedOnS3);

		// 3. Load the image using OpenCV
		Mat originalImage = Imgcodecs.imread(localImagePath);

		if (originalImage.empty()) {
			log.error("Failed to load image: {}", localImagePath);
			throw new FileException(GlobalErrorCode.FILE_DOWNLOAD_FAILED);
		}
		// 4. Generate irregular puzzle pieces
		List<PuzzlePiece> pieces = generateIrregularPuzzlePieces(originalImage, createdPuzzle);

		// 5. Set puzzle pieces to the puzzle
		createdPuzzle.setPuzzlePieces(pieces);

		// 6. Save puzzle pieces
		puzzlePieceRepository.saveAll(pieces);

		// After processing
		File localImageFile = new File(localImagePath);
		if (localImageFile.exists()) {
			localImageFile.delete();
		}

		// 7. Return initial game state (you need to implement this)
		return getCurrentState(createdPuzzle.getId());

	}

	@Transactional(readOnly = true)
	public PuzzleStateMessageResponseDto.PuzzleState getCurrentState(Long puzzleId) {
		// Fetch the puzzle by ID
		Puzzle puzzle = puzzleRepository.findById(puzzleId)
			.orElseThrow(() -> new PuzzleException(GlobalErrorCode.PUZZLE_NOT_FOUND));

		// Get the list of puzzle pieces
		List<PuzzlePiece> pieces = puzzlePieceRepository.findByPuzzleId(puzzleId);

		// Map the puzzle pieces to PieceState DTOs
		List<PuzzleStateMessageResponseDto.PieceState> pieceStates = pieces.stream().map(piece -> {
			return PuzzleStateMessageResponseDto.PieceState.builder()
				.pieceId(piece.getId())
				.currentX(piece.getCurrentX())
				.currentY(piece.getCurrentY())
				.isPlaced(piece.isPlaced())
				.pieceImageUrl(piece.getPieceImageUrl())
				.build();
		}).collect(Collectors.toList());

		// Build the PuzzleState DTO
		return PuzzleStateMessageResponseDto.PuzzleState.builder()
			.puzzleId(puzzleId)
			.pieceStates(pieceStates)
			.build();
	}

	private List<PuzzlePiece> generateIrregularPuzzlePieces(Mat originalImage, Puzzle puzzle) {
		List<PuzzlePiece> pieces = new ArrayList<>();

		int pieceCount = puzzle.getPieceCount();
		int gridSize = (int)Math.sqrt(pieceCount);

		// Dimensions of each piece
		int pieceWidth = originalImage.width() / gridSize;
		int pieceHeight = originalImage.height() / gridSize;

		// Initialize data structures to keep track of edge types
		EdgeType[][][] edgeTypes = new EdgeType[gridSize][gridSize][4]; // 4 edges per piece

		Random random = new Random();

		// First pass: Assign edge types
		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				// For each edge, assign an EdgeType
				// Edges: 0 - top, 1 - right, 2 - bottom, 3 - left
				EdgeType[] edges = new EdgeType[4];

				// Top edge
				if (y == 0) {
					edges[0] = EdgeType.FLAT;
				} else {
					// Complementary to bottom edge of the piece above
					edges[0] = complementaryEdgeType(edgeTypes[y - 1][x][2]);
				}

				// Left edge
				if (x == 0) {
					edges[3] = EdgeType.FLAT;
				} else {
					// Complementary to right edge of the piece to the left
					edges[3] = complementaryEdgeType(edgeTypes[y][x - 1][1]);
				}

				// Right edge
				if (x == gridSize - 1) {
					edges[1] = EdgeType.FLAT;
				} else {
					edges[1] = randomEdgeType(random);
				}

				// Bottom edge
				if (y == gridSize - 1) {
					edges[2] = EdgeType.FLAT;
				} else {
					edges[2] = randomEdgeType(random);
				}

				edgeTypes[y][x] = edges;
			}
		}

		// Second pass: Create the pieces
		for (int y = 0; y < gridSize; y++) {
			for (int x = 0; x < gridSize; x++) {
				// Create mask for this piece
				Mat pieceMask = createPieceMask(pieceWidth, pieceHeight, edgeTypes[y][x]);

				// Define the region of interest (ROI) on the original image
				Rect roi = new Rect(x * pieceWidth, y * pieceHeight, pieceWidth, pieceHeight);
				Mat imageROI = new Mat(originalImage, roi);

				// Apply mask to the ROI
				Mat pieceImage = new Mat();
				imageROI.copyTo(pieceImage, pieceMask);

				// Save piece image to disk or cloud storage
				String pieceImageUrl = savePieceImage(pieceImage, x, y);

				// Create PuzzlePiece entity
				PuzzlePiece piece = PuzzlePiece.builder()
					.puzzle(puzzle)
					.pieceImageUrl(pieceImageUrl)
					.originalX(x * pieceWidth)
					.originalY(y * pieceHeight)
					.currentX(random.nextInt(originalImage.width()))
					.currentY(random.nextInt(originalImage.height()))
					.isPlaced(false)
					.build();

				// Set edge types if needed (optional)
				// piece.setTopEdge(edgeTypes[y][x][0]);
				// piece.setRightEdge(edgeTypes[y][x][1]);
				// piece.setBottomEdge(edgeTypes[y][x][2]);
				// piece.setLeftEdge(edgeTypes[y][x][3]);

				pieces.add(piece);
			}
		}

		return pieces;
	}

	private EdgeType randomEdgeType(Random random) {
		return random.nextBoolean() ? EdgeType.TAB : EdgeType.SLOT;
	}

	private EdgeType complementaryEdgeType(EdgeType edgeType) {
		if (edgeType == EdgeType.TAB) {
			return EdgeType.SLOT;
		} else if (edgeType == EdgeType.SLOT) {
			return EdgeType.TAB;
		} else {
			return EdgeType.FLAT;
		}
	}

	private Mat createPieceMask(int pieceWidth, int pieceHeight, EdgeType[] edges) {
		// Create a larger canvas to accommodate tabs and slots
		int maskWidth = pieceWidth + pieceWidth / 2;
		int maskHeight = pieceHeight + pieceHeight / 2;

		Mat mask = Mat.zeros(maskHeight, maskWidth, CvType.CV_8UC1);

		// Starting point offsets
		int offsetX = pieceWidth / 4;
		int offsetY = pieceHeight / 4;

		// Create a contour for the piece shape
		List<Point> contour = new ArrayList<>();

		// Top-left corner
		Point startPoint = new Point(offsetX, offsetY);

		// Build the contour by adding edges
		contour.addAll(drawEdge(startPoint, edges[0], pieceWidth, true, true)); // Top edge
		contour.addAll(drawEdge(contour.get(contour.size() - 1), edges[1], pieceHeight, false, true)); // Right edge
		contour.addAll(drawEdge(contour.get(contour.size() - 1), edges[2], pieceWidth, true, false)); // Bottom edge
		contour.addAll(drawEdge(contour.get(contour.size() - 1), edges[3], pieceHeight, false, false)); // Left edge

		// Close the contour
		contour.add(startPoint);

		MatOfPoint matOfPoint = new MatOfPoint();
		matOfPoint.fromList(contour);

		List<MatOfPoint> contours = new ArrayList<>();
		contours.add(matOfPoint);

		Imgproc.fillPoly(mask, contours, new Scalar(255));

		return mask.submat(new Rect(offsetX, offsetY, pieceWidth, pieceHeight));
	}

	private List<Point> drawEdge(Point startPoint, EdgeType edgeType, int length, boolean horizontal,
		boolean positiveDirection) {
		List<Point> edgePoints = new ArrayList<>();
		edgePoints.add(startPoint);

		int tabWidth = length / 4;
		int tabHeight = length / 4;

		// Calculate end point
		Point endPoint;
		if (horizontal) {
			endPoint = new Point(startPoint.x + (positiveDirection ? length : -length), startPoint.y);
		} else {
			endPoint = new Point(startPoint.x, startPoint.y + (positiveDirection ? length : -length));
		}

		if (edgeType == EdgeType.FLAT) {
			// Straight line
			edgePoints.add(endPoint);
		} else {
			// Generate points to approximate a curve
			// For example, add multiple points along a sine wave or quadratic function
			int numPoints = 20; // Number of points to approximate the curve
			for (int i = 1; i <= numPoints; i++) {
				double t = (double)i / numPoints;
				// Calculate intermediate points along the curve
				// This is a placeholder; replace with actual curve calculations
				double x = startPoint.x + t * (endPoint.x - startPoint.x);
				double y = startPoint.y + t * (endPoint.y - startPoint.y)
					+ (edgeType == EdgeType.TAB ? -1 : 1) * tabHeight * Math.sin(Math.PI * t);
				edgePoints.add(new Point(x, y));
			}
			edgePoints.add(endPoint);
		}

		return edgePoints;
	}

	private String savePieceImage(Mat pieceImage, int xIndex, int yIndex) {
		try {
			String fileName = "piece_" + xIndex + "_" + yIndex + ".png";

			// Convert Mat to byte array
			MatOfByte mob = new MatOfByte();
			Imgcodecs.imencode(".png", pieceImage, mob);
			byte[] byteArray = mob.toArray();

			// Create an InputStream from the byte array
			InputStream inputStream = new ByteArrayInputStream(byteArray);

			// Create a MultipartFile using your MockMultipartFile class
			MultipartFile multipartFile = new MockMultipartFile(
				fileName,         // name
				fileName,         // originalFilename
				"image/png",      // contentType
				inputStream       // contentStream
			);

			// Upload to S3
			String pieceImageUrl = s3Service.uploadImage(multipartFile);

			return pieceImageUrl;
		} catch (IOException e) {
			log.error("Failed to save and upload piece image", e);
			throw new FileException(GlobalErrorCode.PUZZLE_PIECE_SAVE_FAILED);
		}
	}

}
