package com.ghpg.morningbuddies.global.security.jwt;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.dto.CustomMemberDetails;
import com.ghpg.morningbuddies.auth.member.entity.Member;
import com.ghpg.morningbuddies.auth.member.entity.enums.UserRole;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String accessToken = request.getHeader("access");

		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			sendErrorResponse(response, GlobalErrorCode.ACCESS_TOKEN_EXPIRED);
			return;
		}

		String category = jwtUtil.getCategory(accessToken);
		if (!category.equals("access")) {
			sendErrorResponse(response, GlobalErrorCode.INVALID_ACCESS_TOKEN);
			return;
		}

		String email = jwtUtil.getEmail(accessToken);
		String role = jwtUtil.getRole(accessToken);

		Member member = Member.builder()
			.email(email)
			.userRole(UserRole.valueOf(role))
			.build();

		CustomMemberDetails customUserDetails = new CustomMemberDetails(member);
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}

	private void sendErrorResponse(HttpServletResponse response, GlobalErrorCode errorCode) throws IOException {
		CommonResponse<String> errorResponse = CommonResponse.onFailure(
			errorCode.getCode(),
			errorCode.getMessage(),
			null
		);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}
