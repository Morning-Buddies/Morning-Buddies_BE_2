package com.ghpg.morningbuddies.global.aws.s3;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.file.FileException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

	private final S3Client s3Client;
	private final String bucketName;

	@Value("${aws.s3.url}")
	private String s3Url;

	/**
	 * 프로필 이미지 업로드
	 * @param file
	 * @return
	 */
	@Override
	public String uploadImage(MultipartFile file) {
		String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
		return uploadFile(fileName, file);
	}

	/**
	 * 파일 업로드
	 * @param fileName
	 * @param file
	 * @return
	 */
	private String uploadFile(String fileName, MultipartFile file) {
		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return s3Url + "/" + fileName;
		} catch (IOException e) {
			throw new FileException(GlobalErrorCode.FILE_UPLOAD_FAILED);
		}
	}

}