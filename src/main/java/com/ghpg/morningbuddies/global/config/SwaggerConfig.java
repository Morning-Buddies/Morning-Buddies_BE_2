package com.ghpg.morningbuddies.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {

		// Server server = new Server();
		// server.setUrl("https://dev.morningbuddies.shop");

		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		SecurityRequirement securityRequirement =
			new SecurityRequirement().addList("bearer-jwt");

		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("bearer-jwt", securityScheme))
			.security(List.of(securityRequirement))  // 이 부분이 추가됨
			.info(new Info()
				.title("Morning Buddies API")
				.description("Morning Buddies API 명세서")
				.version("v1.0.0"));
		// .addServersItem(server);
	}
}