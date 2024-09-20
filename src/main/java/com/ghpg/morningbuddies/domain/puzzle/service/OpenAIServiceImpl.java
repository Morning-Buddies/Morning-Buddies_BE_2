package com.ghpg.morningbuddies.domain.puzzle.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OpenAIServiceImpl implements OpenAIService {

	private final RestTemplate restTemplate;

	@Value("${openai.api.key}")
	private String apiKey;

	@PostConstruct
	public void init() {
		log.info("API Key initialized: {}", apiKey != null ? apiKey.substring(0, 5) + "..." : "null");
	}

	@Override
	public String generateImage(String prompt) {
		String url = "https://api.openai.com/v1/images/generations";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + apiKey);

		Map<String, Object> requestBody = Map.of(
			"model", "dall-e-3",
			"prompt", prompt,
			"size", "1024x1024",
			"style", "natural",
			"n", 1
		);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

		log.info("Sending request to OpenAI API. URL: {}, Headers: {}, Body: {}", url, headers, requestBody);

		try {
			ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);
			Map<String, Object> response = responseEntity.getBody();

			log.info("Received response from OpenAI API. Status: {}, Body: {}", responseEntity.getStatusCode(),
				response);

			if (response != null && response.containsKey("data")) {
				List<Map<String, String>> data = (List<Map<String, String>>)response.get("data");
				if (!data.isEmpty()) {
					String imageUrl = data.get(0).get("url");
					log.info("Generated image URL: {}", imageUrl);
					return imageUrl;
				}
			}
			log.warn("Unexpected response format from OpenAI API");
			return null;
		} catch (HttpClientErrorException e) {
			log.error("Error calling OpenAI API. Status: {}, Response: {}", e.getStatusCode(),
				e.getResponseBodyAsString());
			throw new RuntimeException("Error generating image: " + e.getStatusText(), e);
		}
	}
}
