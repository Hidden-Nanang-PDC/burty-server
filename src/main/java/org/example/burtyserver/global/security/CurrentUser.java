package org.example.burtyserver.global.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

/**
 * 현재 인증된 사용자 정보를 컨트롤러 메서드 파라미터로 주입하기 위한 어노테이션
 * Spring Security의 @AuthenticationPrincipal을 확장하여 사용
 *
 * 사용 예:
 * public ResponseEntity getCurrentUser(@CurrentUser UserPrincipal userPrincipal) { ... }
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})  // 파라미터와 타입에 사용 가능
@Retention(RetentionPolicy.RUNTIME)                 // 런타임에 유지
@Documented                                         // Javadoc에 포함
@AuthenticationPrincipal                            // Spring Security의 인증 주체 주입
public @interface CurrentUser {
}
