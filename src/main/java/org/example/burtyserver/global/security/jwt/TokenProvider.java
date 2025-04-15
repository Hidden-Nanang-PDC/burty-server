package org.example.burtyserver.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 서비스 클래스
 * 인증 정보로부터 JWT 토큰을 생성하고, 토큰의 유효성을 검증
 */
@Slf4j
@Service
public class TokenProvider {

    @Value("${app.auth.token-secret}")
    private String tokenSecret;  // JWT 서명에 사용할 비밀키

    @Value("${app.auth.token-expiration-msec}")
    private long tokenExpirationMsec;  // 토큰 만료 시간(밀리초)

    /**
     * 인증 정보로부터 JWT 토큰 생성
     *
     * @param authentication Spring Security 인증 객체
     * @return 생성된 JWT 토큰 문자열
     */
    public String createToken(Authentication authentication) {
        // UserPrincipal로 캐스팅
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        // 사용자 권한 정보를 쉼표로 구분된 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(","));

        // JWT 서명을 위한 키 생성
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))  // 사용자 ID
                .claim("authorities", authorities)             // 권한 정보
                .claim("email", userPrincipal.getEmail())    // 이메일
                .setIssuedAt(new Date())                          // 토큰 발행 시간
                .setExpiration(expiryDate)                        // 토큰 만료 시간
                .signWith(key)                                 // 서명 키
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        // JWT 서명 검증을 위한 키 생성
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Subject 클레임에서 사용자 ID 추출
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param authToken 검증할 JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String authToken) {
        try {
            // JWT 서명 검증을 위한 키 생성
            Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());
            // 토큰 파싱 및 검증
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }
}