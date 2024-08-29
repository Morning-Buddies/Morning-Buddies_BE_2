package com.ghpg.morningbuddies.global.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.global.security.jwt.CustomLogoutFilter;
import com.ghpg.morningbuddies.global.security.jwt.JwtFilter;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;
import com.ghpg.morningbuddies.global.security.jwt.LoginFilter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;

	private final JwtUtil jwtUtil;

	private final ObjectMapper objectMapper;

	private final RefreshTokenRepository refreshTokenRepository;

	private final MemberRepository memberRepository;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration,
		MemberRepository memberRepository) throws Exception {

		AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

		LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil, objectMapper, refreshTokenRepository,
			memberRepository);
		loginFilter.setFilterProcessesUrl("/auth/login"); // 로그인 처리 URL 설정 (필요에 따라 변경)
		//csrf disable
		http
			.csrf((auth) -> auth.disable());

		//From 로그인 방식 disable
		http
			.formLogin((auth) -> auth.disable());

		//http basic 인증 방식 disable
		http
			.httpBasic((auth) -> auth.disable());

		//경로별 인가 작업
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/ws/**").permitAll()
				.requestMatchers("/", "health", "/auth/**").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated());

		http
			.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

		http
			.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

		http
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository, objectMapper), LogoutFilter.class);
		//세션 설정
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// CORS 설정
		http
			.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

				@Override
				public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

					CorsConfiguration configuration = new CorsConfiguration();

					configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
					configuration.setAllowedMethods(Collections.singletonList("*"));
					configuration.setAllowCredentials(true);
					configuration.setAllowedHeaders(Collections.singletonList("*"));
					configuration.setMaxAge(3600L);

					configuration.setExposedHeaders(Collections.singletonList("Authorization"));

					return configuration;
				}
			})));

		return http.build();
	}
}