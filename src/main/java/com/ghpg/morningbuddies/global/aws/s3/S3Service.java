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

	/**
	 * URL에서 이미지 다운로드 후 S3에 업로드
	 * @param imageUrl
	 * @return
	 */
	String transferImageToS3(String imageUrl);

	/**
	 * S3에서 이미지 다운로드
	 * @param imageUrl
	 * @return
	 */
	String downloadImageFromS3(String imageUrl);
}
