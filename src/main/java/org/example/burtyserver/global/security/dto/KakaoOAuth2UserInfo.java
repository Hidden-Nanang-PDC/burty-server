package org.example.burtyserver.global.security.dto;

import java.util.Map;

/**
 * 카카오 OAuth2 사용자 정보 처리 클래스
 * 카카오 API 응답 형식에 맞게 사용자 정보를 추출
 */
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        if (attributes.get("id") == null) {
            return "kakao_unknown";  // 기본값 제공
        }
        return attributes.get("id").toString();
    }


    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
                return (String) kakaoAccount.get("email");
            }
        }
        // 이메일이 없는 경우 null 반환 (상위 메소드에서 처리)
        return null;
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return (String) properties.get("profile_image");
    }
}