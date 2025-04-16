package org.example.burtyserver.global.security.dto;

import java.util.Map;

/**
 * 구글 OAuth2 사용자 정보 처리 클래스
 * 구글 API 응답 형식에 맞게 사용자 정보를 추출
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}