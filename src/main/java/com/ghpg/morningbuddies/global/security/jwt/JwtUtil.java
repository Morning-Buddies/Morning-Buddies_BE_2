package com.ghpg.morningbuddies.global.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey secretKey;

	public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
	}

	public String getUsername(String token) {
		return getClaim(token, "email");
	}

	public String getEmail(String token) {
		return getClaim(token, "email");
	}

	public String getRole(String token) {
		return getClaim(token, "role");
	}

	public Boolean isExpired(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getExpiration()
				.before(new Date());
		} catch (JwtException e) {
			return true;
		}
	}

	public String getCategory(String token) {
		return getClaim(token, "category");
	}

	public String createJwt(String category, String email, String role, Long expiredMs) {
		return Jwts.builder()
			.claim("category", category)
			.claim("email", email)
			.claim("role", role)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + expiredMs))
			.signWith(secretKey)
			.compact();
	}

	private String getClaim(String token, String claimName) {
		try {
			Claims claims = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return claims.get(claimName, String.class);
		} catch (JwtException e) {
			return null;
		}
	}
}