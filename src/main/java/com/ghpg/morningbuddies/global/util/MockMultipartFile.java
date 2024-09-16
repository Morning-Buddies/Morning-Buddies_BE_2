package com.ghpg.morningbuddies.global.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

// Base64 디코딩된 이미지를 MultipartFile로 변환하기 위한 유틸리티 클래스
public class MockMultipartFile implements MultipartFile {

	private final String name;
	private final String originalFilename;
	private final String contentType;
	private final byte[] content;

	public static MultipartFile base64ToMultipartFile(String base64Image) {
		try {
			String[] baseStrs = base64Image.split(",");
			byte[] bytes = Base64.getDecoder().decode(baseStrs.length > 1 ? baseStrs[1] : baseStrs[0]);
			return new MockMultipartFile("image.png", "image.png", "image/png", new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			throw new RuntimeException("Failed to convert base64 to MultipartFile", e);
		}
	}

	public MockMultipartFile(String name, String originalFilename, String contentType, InputStream contentStream) throws
		IOException {
		this.name = name;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
		this.content = contentStream.readAllBytes();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public boolean isEmpty() {
		return content.length == 0;
	}

	@Override
	public long getSize() {
		return content.length;
	}

	@Override
	public byte[] getBytes() throws IOException {
		return content;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		throw new UnsupportedOperationException("transferTo() is not supported");
	}
}