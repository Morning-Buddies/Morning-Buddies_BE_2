package com.ghpg.morningbuddies.global.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ghpg.morningbuddies.global.exception.JwtException.JwtException;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
	private SecretKey secretKey;
	private static final String EMAIL_CLAIM = "email";
	private static final String ROLE_CLAIM = "role";
	private static final String TOKEN_TYPE_CLAIM = "tokenType";
	private static final String BEARER_PREFIX = "Bearer ";

	// Token expiration times
	public static final long ACCESS_TOKEN_EXPIRATION_MS = 36000000; // 10시간
	public static final long REFRESH_TOKEN_EXPIRATION_MS = 1209600000; // 14일

	// Token types
	private static final String ACCESS_TOKEN = "ACCESS";
	private static final String REFRESH_TOKEN = "REFRESH";

	public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
		this.secretKey = new SecretKeySpec(
			secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm()
		);
	}

	public String getEmail(String token) {
		try {
			return getClaims(token).get(EMAIL_CLAIM, String.class);
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public String getRole(String token) {
		try {
			return getClaims(token).get(ROLE_CLAIM, String.class);
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public String getTokenType(String token) {
		try {
			return getClaims(token).get(TOKEN_TYPE_CLAIM, String.class);
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public Boolean isExpired(String token) {
		try {
			return getClaims(token)
				.getExpiration()
				.before(new Date());
		} catch (ExpiredJwtException e) {
			throw new JwtException(GlobalErrorCode.TOKEN_EXPIRED);
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public String createAccessToken(String email, String role) {
		return createJwt(email, role, ACCESS_TOKEN_EXPIRATION_MS, ACCESS_TOKEN);
	}

	public String createRefreshToken(String email, String role) {
		return createJwt(email, role, REFRESH_TOKEN_EXPIRATION_MS, REFRESH_TOKEN);
	}

	private String createJwt(String email, String role, Long expiredMs, String tokenType) {
		try {
			return Jwts.builder()
				.claim(EMAIL_CLAIM, email)
				.claim(ROLE_CLAIM, role)
				.claim(TOKEN_TYPE_CLAIM, tokenType)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiredMs))
				.signWith(secretKey)
				.compact();
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public String extractToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(BEARER_PREFIX.length());
		}
		throw new JwtException(GlobalErrorCode.EMPTY_TOKEN);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);
			return true;
		} catch (SignatureException e) {
			throw new JwtException(GlobalErrorCode.INVALID_SIGNATURE);
		} catch (ExpiredJwtException e) {
			throw new JwtException(GlobalErrorCode.TOKEN_EXPIRED);
		} catch (UnsupportedJwtException e) {
			throw new JwtException(GlobalErrorCode.UNSUPPORTED_TOKEN);
		} catch (IllegalArgumentException e) {
			throw new JwtException(GlobalErrorCode.EMPTY_TOKEN);
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}

	public boolean isAccessToken(String token) {
		return ACCESS_TOKEN.equals(getTokenType(token));
	}

	public boolean isRefreshToken(String token) {
		return REFRESH_TOKEN.equals(getTokenType(token));
	}

	private Claims getClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (Exception e) {
			throw new JwtException(GlobalErrorCode.INVALID_TOKEN);
		}
	}
}