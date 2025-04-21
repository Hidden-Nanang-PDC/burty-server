package org.example.burtyserver.global.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.global.security.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 인증 성공 시 처리를 담당하는 핸들러
 * 로그인 성공 후 JWT 토큰을 생성하고 프론트엔드 리다이렉트 URL로 토큰과 함께 리다이렉트
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    @Value("${app.auth.refresh-token-expiration-msec}")
    private long refreshTokenExpirationMsec;

    /**
     * 인증 성공 시 호출되는 메서드
     * JWT 토큰을 생성하고 프론트엔드로 리다이렉트
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // Access Token 생성
        String token = tokenProvider.createToken(authentication);

        // Refresh Token 생성
        String refreshToken = tokenProvider.createRefreshToken(authentication);
        addRefreshTokenCookie(response, refreshToken);

        // Access Token을 포함해 리다이렉트
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .build().toUriString();

        // 이미 응답이 커밋된 경우, 로그만 남기고 종료
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        int maxAge = (int) (refreshTokenExpirationMsec/1000);

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

}
