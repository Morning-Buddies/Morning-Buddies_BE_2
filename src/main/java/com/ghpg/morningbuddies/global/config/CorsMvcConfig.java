package com.ghpg.morningbuddies.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins(
				"http://localhost:3000",
				"http://127.0.0.1:3000",
				"https://dev.morningbuddies.shop"
			)
			.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
			.allowedHeaders("*")
			.exposedHeaders("access")  // access 헤더를 노출
			.allowCredentials(true)  // 쿠키를 포함한 요청 허용
			.maxAge(3600);
	}
}