package com.ghpg.morningbuddies.global.aws.s3;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {

	/**
	 * 프로필 이미지 업로드
	 * @param file
	 * @return
	 * @throws IOException
	 */
	String uploadImage(MultipartFile file);
}
