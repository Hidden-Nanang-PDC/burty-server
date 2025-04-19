package org.example.burtyserver.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.example.burtyserver.domain.auth.model.entity.RefreshToken;
import org.example.burtyserver.domain.auth.model.repository.RefreshTokenRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 서비스 클래스
 * 인증 정보로부터 JWT 토큰을 생성하고, 토큰의 유효성을 검증
 */
@Slf4j
@Service
public class TokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${app.auth.token-secret}")
    private String tokenSecret;  // JWT 서명에 사용할 비밀키

    @Value("${app.auth.token-expiration-msec}")
    private long tokenExpirationMsec;  // JWT 서명에 사용할 비밀키

    @Value("${app.auth.refresh-token-expiration-msec}")
    private long refreshTokenExpirationMsec;  // JWT 서명에 사용할 비밀키

    public TokenProvider(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository){
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }


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
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // JWT 서명을 위한 키 생성
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        // JWT 토큰 생성
        return Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))  // 사용자 ID
                .claim("authorities", authorities)             // 권한 정보
                .claim("email", userPrincipal.getEmail())    // 이메일
                .issuedAt(new Date())                          // 토큰 발행 시간
                .expiration(expiryDate)                        // 토큰 만료 시간
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
        SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

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
            SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes());
            // 토큰 파싱 및 검증
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
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


    /**
     * 인증 정보로부터 Refresh Token 생성
     *
     * @param authentication Spring Security 인증 객체
     * @return 생성된 Refresh Token 문자열
     */
    public String createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 기존 Refresh Token이 있으면 폐기
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserAndRevokedFalse(user);
        existingToken.ifPresent(RefreshToken::revoke);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMsec);
        LocalDateTime expiresAt = expiryDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // JWT 서명 key
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        // Refresh Token 생성
        String refreshTokenString = Jwts.builder()
                .subject(Long.toString(userPrincipal.getId()))
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        // Refresh Token DB 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenString)
                .user(user)
                .revoked(false)
                .expiresAt(expiresAt)
                .build();
        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }

    /**
     * Refresh Token 검증
     *
     * @param refreshToken Refresh Token
     * @return 유효성 여부
     */
    public boolean validateRefreshToken(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(tokenSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken);

            Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
            return tokenEntity.isPresent() && !tokenEntity.get().isRevoked();
        } catch (Exception ex) {
            log.error("Refresh token validation error", ex);
            return false;
        }

    }

    /**
     * Refresh Token 폐기
     *
     * @param refreshToken Refresh Token
     */
    public void revokeRefreshToken(String refreshToken) {
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
        tokenEntity.ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
    }
}