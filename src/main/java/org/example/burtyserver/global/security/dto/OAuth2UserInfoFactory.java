package org.example.burtyserver.global.security.dto;

import org.example.burtyserver.global.exception.OAuth2AuthenticationException;

import java.util.Map;

/**
 * OAuth2 제공자에 따라 적절한 사용자 정보 처리 클래스를 생성하는 팩토리 클래스
 * 각 소셜 로그인 제공자별 특화된 UserInfo 구현체를 반환
 */
public class OAuth2UserInfoFactory {

    /**
     * 소셜 로그인 제공자 ID와 속성을 받아 적절한 OAuth2UserInfo 구현체를 반환
     *
     * @param registrationId 소셜 로그인 제공자 ID (kakao, google, naver)
     * @param attributes 소셜 로그인 제공자로부터 받은 사용자 속성 정보
     * @return 제공자에 맞는 OAuth2UserInfo 구현체
     * @throws OAuth2AuthenticationException 지원하지 않는 제공자인 경우 발생
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("kakao")) {
            return new KakaoOAuth2UserInfo(attributes);
//        } else if (registrationId.equalsIgnoreCase("google")) {
//            return new GoogleOAuth2UserInfo(attributes);
//        } else if (registrationId.equalsIgnoreCase("naver")) {
//            return new NaverOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}