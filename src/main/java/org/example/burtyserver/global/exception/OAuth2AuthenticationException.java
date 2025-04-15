package org.example.burtyserver.global.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * OAuth2 인증 관련 예외 클래스
 * Spring Security의 AuthenticationException을 확장하여 OAuth2 인증 실패 시 사용
 */
public class OAuth2AuthenticationException extends AuthenticationException {

    /**
     * 메시지만 포함하는 생성자
     *
     * @param msg 예외 메시지
     */
    public OAuth2AuthenticationException(String msg) {
        super(msg);
    }

    /**
     * 메시지와 원인 예외를 포함하는 생성자
     *
     * @param msg 예외 메시지
     * @param t 원인 예외
     */
    public OAuth2AuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }
}