package com.ghpg.morningbuddies.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghpg.morningbuddies.auth.member.repository.RefreshTokenRepository;
import com.ghpg.morningbuddies.global.common.CommonResponse;
import com.ghpg.morningbuddies.global.exception.common.code.GlobalErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!"/auth/logout".equals(request.getRequestURI()) || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = extractRefreshTokenFromCookie(request);

        if (refresh == null) {
            sendErrorResponse(response, GlobalErrorCode.REFRESH_TOKEN_REQUIRED);
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, GlobalErrorCode.REFRESH_TOKEN_EXPIRED);
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            sendErrorResponse(response, GlobalErrorCode.INVALID_REFRESH_TOKEN);
            return;
        }

        if (!refreshTokenRepository.existsByRefresh(refresh)) {
            sendErrorResponse(response, GlobalErrorCode.REFRESH_TOKEN_NOT_FOUND);
            return;
        }

        // 로그아웃 진행
        refreshTokenRepository.deleteByRefresh(refresh);

        // Refresh 토큰 Cookie 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        sendSuccessResponse(response);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, GlobalErrorCode errorCode) throws IOException {
        CommonResponse<String> errorResponse = CommonResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
        sendJsonResponse(response, HttpServletResponse.SC_BAD_REQUEST, errorResponse);
    }

    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        CommonResponse<String> successResponse = CommonResponse.onSuccess("Logout successful");
        sendJsonResponse(response, HttpServletResponse.SC_OK, successResponse);
    }

    private void sendJsonResponse(HttpServletResponse response, int status, CommonResponse<?> commonResponse) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
    }
}