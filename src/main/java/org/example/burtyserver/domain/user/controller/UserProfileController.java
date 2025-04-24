package org.example.burtyserver.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.user.model.dto.UserProfileUpdateRequest;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.service.UserService;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name="사용자 프로필 업데이트", description = "사용자 닉네임, 지역, 나이 업데이트")
public class UserProfileController {
    private final UserService userService;

    @PutMapping("/profile")
    @Operation(
            summary = "사용자 프로필 업데이트",
            description = "JWT 토큰이 필요하며, 현재 로그인된 유저의 프로필 정보를 추가한다.",
            security = @SecurityRequirement(name="bearerAuth")
    )
    public ResponseEntity<?> updateUserProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody UserProfileUpdateRequest request){
        User user = userService.updateUserProfile(
                userPrincipal.getId(),
                request.getNickname(),
                request.getRegion(),
                request.getBirthDate(),
                request.getJob()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "프로필이 업데이트되었습니다.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인된 사용자의 계정을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest req,
            HttpServletResponse resp
    ){
        userService.deleteAccount(userPrincipal.getId(), req, resp);

        // Access Token을 무효화하기 위해 쿠키 삭제
        Cookie jwtCookie = new Cookie("Authorization", null);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        resp.addCookie(jwtCookie);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "회원 탈퇴가 완료되었습니다.");

        return ResponseEntity.ok(response);
    }
}
