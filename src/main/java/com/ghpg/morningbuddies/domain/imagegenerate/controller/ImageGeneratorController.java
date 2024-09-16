package com.ghpg.morningbuddies.domain.imagegenerate.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageRequestDto;
import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageResponseDto;
import com.ghpg.morningbuddies.domain.imagegenerate.service.ImageGenerateCommandService;
import com.ghpg.morningbuddies.global.common.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageGeneratorController {

	private final ImageGenerateCommandService imageGenerateCommandService;

	@PostMapping("/generate-puzzle")
	public CommonResponse<ImageResponseDto.GeneratePuzzleImage> generatePuzzleImage(
		@RequestBody @Valid ImageRequestDto.GeneratePuzzleImage request) {

		log.info("generatePuzzleImage request: {}", request);

		return CommonResponse.onSuccess(imageGenerateCommandService.generatePuzzleImage(request));
	}
}
