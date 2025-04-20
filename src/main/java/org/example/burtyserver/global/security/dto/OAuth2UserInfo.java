package org.example.burtyserver.global.security.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * OAuth2 제공자로부터 받은 사용자 정보를 표준화하는 추상 클래스
 * 각 소셜 로그인 제공자별로 다른 응답 형식을 통일된 인터페이스로 제공
 */
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;  // OAuth2 제공자로부터 받은 원본 속성 정보

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();
    public abstract String getName();
    public abstract String getEmail();
    public abstract String getImageUrl();

    // 소셜 로그인에서 제공하지 않는 정보는 null을 반환
    public String getNickname() {return null;}
    public String getReion() {return null;}
    public LocalDate getBirthDate() {return null;}
}