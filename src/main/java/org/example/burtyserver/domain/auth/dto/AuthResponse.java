package org.example.burtyserver.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 응답 DTO
 * 클라이언트에게 전달할 사용자 인증 정보를 담는 객체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;        // 사용자 ID
    private String email;   // 사용자 이메일
    private String name;    // 사용자 이름
    private String imageUrl;  // 사용자 프로필 이미지 URL
    private String provider;  // 인증 제공자 (KAKAO, GOOGLE, NAVER, LOCAL)
}