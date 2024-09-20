package com.ghpg.morningbuddies.domain.puzzle.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
	"aws.access_key=${AWS_S3_ACCESS_KEY}",
	"aws.secret_key=${AWS_S3_SECRET_KEY}",
	"aws.s3.bucket_name=${AWS_S3_BUCKET_NAME}",
	"aws.s3.url=${AWS_S3_URL}",
	"aws.region=${AWS_REGION}",
	"openai.api.key=${OPENAI_API_KEY}"
})
class OpenAIServiceImplTest {

	@Autowired
	private OpenAIService openAIService;

	@Test
	public void generateImage() {
		String imageUrl = openAIService.generateImage("A happy person's face");
		assertNotNull(imageUrl);
		assertTrue(imageUrl.startsWith("http"));
	}

	@Test
	void checkEnvironmentVariable() {
		String apiKey = System.getenv("OPENAI_API_KEY");
		assertNotNull(apiKey, "OPENAI_API_KEY environment variable is not set");
		assertFalse(apiKey.isEmpty(), "OPENAI_API_KEY environment variable is empty");
	}
}