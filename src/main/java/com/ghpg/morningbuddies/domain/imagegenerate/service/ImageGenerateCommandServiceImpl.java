package com.ghpg.morningbuddies.domain.imagegenerate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageRequestDto;
import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageResponseDto;
import com.ghpg.morningbuddies.global.aws.s3.S3Service;
import com.ghpg.morningbuddies.global.util.MockMultipartFile;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageGenerateCommandServiceImpl implements ImageGenerateCommandService {

	private final OpenAiService openAiService;
	private final S3Service s3Service;

	/**
	 * 주어진 prompt를 이용하여 퍼즐 이미지를 생성하여 S3에 업로드하고, 이미지 URL을 반환한다.
	 * @param request
	 * @return
	 */
	@Override
	public ImageResponseDto.GeneratePuzzleImage generatePuzzleImage(ImageRequestDto.GeneratePuzzleImage request) {
		String prompt = request.getPrompt();

		CreateImageRequest imageRequest = CreateImageRequest.builder()
			.prompt(request.getPrompt())
			.n(1)
			.size("1024x1024")
			.responseFormat("b64_json")
			.build();

		// OpenAI API를 이용하여 이미지 생성
		ImageResult result = openAiService.createImage(imageRequest);

		// 생성된 이미지의 Base64 데이터
		String base64Image = result.getData().get(0).getB64Json();

		// Base64 이미지를 MultipartFile로 변환
		MultipartFile file = MockMultipartFile.base64ToMultipartFile(base64Image);

		// S3에 이미지 업로드
		String imageUrl = s3Service.uploadImage(file);

		return ImageResponseDto.GeneratePuzzleImage.builder()
			.imageUrl(imageUrl)
			.build();
	}
}
