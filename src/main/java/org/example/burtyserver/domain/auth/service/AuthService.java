package org.example.burtyserver.domain.auth.service;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.example.burtyserver.global.security.jwt.TokenProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 비즈니스 로직을 담당하는 서비스 클래스.
 * 주로 로그아웃 처리 및 리프레시 토큰 관련 기능을 수행한다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    /**
     * 사용자 로그아웃 처리
     * 리프레시 토큰을 무효화하고, 해당 쿠키를 클라이언트에서 제거한다.
     *
     * @param userPrincipal 현재 인증된 사용자 정보
     * @param request       클라이언트로부터의 HTTP 요청
     * @param response      클라이언트에게 전달할 HTTP 응답
     */
    public void logout(UserPrincipal userPrincipal, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromRequest(request);

        if (refreshToken != null) {
            tokenProvider.revokeRefreshToken(refreshToken);
            deleteRefreshTokenCookie(response);
        }

        //인증 정보 제거
        SecurityContextHolder.clearContext();

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 쿠키 삭제
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    /**
     * HTTP 요청에서 "refresh_token" 쿠키를 추출한다/
     *
     * @param request 클라이언트 요청
     * @return 쿠키에 저장된 refresh token 값, 없으면 null
     */

    private String extractRefreshTokenFromRequest(HttpServletRequest request) {
        // 요청에서 Refresh Token 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 클라이언트의 refresh token 쿠키를 삭제한다.
     * 쿠키의 maxAge를 0으로 설정하여 브라우저에서 제거되도록 한다.
     *
     * @param response 클라이언트에게 전달할 HTTP 응답
     */
    private void deleteRefreshTokenCookie(HttpServletResponse response) {
        // 클라이언트에서 Refresh Token 쿠키 삭제
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        response.addCookie(cookie);
    }
}
