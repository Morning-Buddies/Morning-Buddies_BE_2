package com.ghpg.morningbuddies.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// Enable a simple in-memory broker with multiple destination prefixes
		config.enableSimpleBroker("/sub", "/topic", "/queue");

		// Set application destination prefixes for messages bound for @MessageMapping
		config.setApplicationDestinationPrefixes("/pub", "/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws-stomp")
			.setAllowedOriginPatterns("*") // 프로덕션 환경에서는 특정 도메인으로 제한하세요
			.withSockJS();
	}

}