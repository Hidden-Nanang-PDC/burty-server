package org.example.burtyserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.auth.dto.AuthResponse;
import org.example.burtyserver.domain.user.entity.User;
import org.example.burtyserver.domain.user.repository.UserRepository;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API 컨트롤러
 * 로그인 상태 확인, 사용자 정보 조회 등 인증 관련 엔드포인트 제공
 */
@RestController
@RequestMapping("/api/auth")  // 기본 경로: /api/auth
@RequiredArgsConstructor
@Tag(name="인증", description = "사용자 인증 관련 API")
public class AuthController {

    private final UserRepository userRepository;  // 사용자 정보 조회를 위한 리포지토리

    /**
     * 현재 인증된 사용자 정보 조회 API
     *
     * @param userPrincipal 현재 인증된 사용자 정보 (@CurrentUser 어노테이션으로 주입)
     * @return 사용자 정보 응답 DTO
     */
    @Operation(summary = "현재 사용자 정보 조회", description = "토큰으로 인증된 현재 사용자의 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content)
    })

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        // 사용자 ID로 상세 정보 조회
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // AuthResponse DTO 생성하여 반환
        return ResponseEntity.ok(
                AuthResponse.builder()
                        .id(user.getId())             // 사용자 ID
                        .email(user.getEmail())       // 이메일
                        .name(user.getName())         // 이름
                        .imageUrl(user.getProfileImageUrl())  // 프로필 이미지 URL
                        .provider(user.getProvider().name())  // 인증 제공자 (KAKAO, GOOGLE, NAVER)
                        .build()
        );
    }
}