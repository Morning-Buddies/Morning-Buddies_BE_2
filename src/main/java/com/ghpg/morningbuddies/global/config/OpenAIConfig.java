package com.ghpg.morningbuddies.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class OpenAIConfig {

	@Value("${openai.api.key}")
	private String openaiApiKey;

	@Bean
	public OpenAiService openAiService(@Value("${openai.api.key}") String openaiApiKey) {
		return new OpenAiService(openaiApiKey, Duration.ofSeconds(120));  // 타임아웃을 60초로 설정
	}
}