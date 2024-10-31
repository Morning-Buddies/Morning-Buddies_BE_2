package com.ghpg.morningbuddies.global.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

@Configuration
public class JacksonConfig {

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		// JavaTimeModule 생성 및 LocalTime 포맷터 설정
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalTime.class,
			new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalTime.class,
			new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));

		objectMapper.registerModule(javaTimeModule);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return objectMapper;
	}

}