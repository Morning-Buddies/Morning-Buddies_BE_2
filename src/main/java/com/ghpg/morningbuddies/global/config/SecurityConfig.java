package com.ghpg.morningbuddies.global.config;

import java.util.Arrays;

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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.repository.MemberRepository;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.global.security.jwt.CustomLogoutFilter;
import com.ghpg.morningbuddies.global.security.jwt.JwtFilter;
import com.ghpg.morningbuddies.global.security.jwt.JwtUtil;
import com.ghpg.morningbuddies.global.security.jwt.LoginFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

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
		loginFilter.setFilterProcessesUrl("/auth/login");

		http
			.csrf((auth) -> auth.disable())
			.formLogin((auth) -> auth.disable())
			.httpBasic((auth) -> auth.disable())
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/", "/health", "/auth/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated())
			.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class)
			.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository, objectMapper), LogoutFilter.class)
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// CORS 설정
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setMaxAge(3600L);
		configuration.setExposedHeaders(Arrays.asList("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}