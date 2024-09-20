package com.ghpg.morningbuddies.global.aws.s3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import com.ghpg.morningbuddies.global.exception.file.FileException;
import com.ghpg.morningbuddies.global.util.MockMultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
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
	 * URL에서 이미지 다운로드 후 S3에 업로드
	 * @param imageUrl
	 * @return
	 */
	@Override
	public String transferImageToS3(String imageUrl) {
		try {
			// 1. URL에서 이미지 다운로드
			Resource resource = new UrlResource(imageUrl);

			// 2. Resource를 MultipartFile로 변환
			Path path = Paths.get(resource.getURI());
			String contentType = determineContentType(path.getFileName().toString());
			MultipartFile multipartFile = new MockMultipartFile(path.getFileName().toString(),
				path.getFileName().toString(), contentType, resource.getInputStream());

			// 3. S3에 업로드
			return uploadImage(multipartFile);
		} catch (MalformedURLException e) {
			log.error("Invalid URL: {}", imageUrl, e);
			throw new FileException(GlobalErrorCode.FILE_DOWNLOAD_FAILED);
		} catch (IOException e) {
			log.error("Failed to transfer image from {} to S3", imageUrl, e);
			throw new FileException(GlobalErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	/**
	 * S3에서 이미지 다운로드
	 * @param imageUrl
	 * @return
	 */
	@Override
	public String downloadImageFromS3(String imageUrl) {
		try {
			// Extract the key (file name) from the S3 URL
			String fileName = s3Url.substring(s3Url.lastIndexOf("/") + 1);

			// Create a GetObjectRequest
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketName)
				.key(fileName)
				.build();

			// Define the local file path where the image will be saved
			String localFilePath = "temp/" + fileName; // Ensure the 'temp' directory exists

			// Download the file from S3
			s3Client.getObject(getObjectRequest, Paths.get(localFilePath));

			return localFilePath;
		} catch (Exception e) {
			log.error("Failed to download image from S3: {}", s3Url, e);
			throw new FileException(GlobalErrorCode.FILE_DOWNLOAD_FAILED);
		}
	}

	private String determineContentType(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		switch (extension) {
			case "jpg":
			case "jpeg":
				return "image/jpeg";
			case "png":
				return "image/png";
			case "gif":
				return "image/gif";
			default:
				return "application/octet-stream";
		}
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