package com.ghpg.morningbuddies.domain.imagegenerate.service;

import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageRequestDto;
import com.ghpg.morningbuddies.domain.imagegenerate.dto.ImageResponseDto;

public interface ImageGenerateCommandService {

	ImageResponseDto.GeneratePuzzleImage generatePuzzleImage(ImageRequestDto.GeneratePuzzleImage request);
}
