package org.example.burtyserver.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.service.CommentLikeService;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 댓글 좋아요 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/community/comments/{commentId}/likes")
@RequiredArgsConstructor
@Tag(name = "댓글 좋아요", description = "댓글 좋아요 관련 API")
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @PostMapping
    @Operation(
            summary = "댓글 좋아요 추가",
            description = "댓글에 좋아요를 추가합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> addLike(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long commentId
    ) {
        boolean result = commentLikeService.addLike(userPrincipal.getId(), commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "좋아요가 추가되었습니다." : "이미 좋아요가 있습니다.");
        response.put("likeCount", commentLikeService.getLikeCount(commentId));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(
            summary = "댓글 좋아요 취소",
            description = "댓글의 좋아요를 취소합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> removeLike(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long commentId
    ) {
        boolean result = commentLikeService.removeLike(userPrincipal.getId(), commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", result);
        response.put("message", result ? "좋아요가 취소되었습니다." : "좋아요가 없습니다.");
        response.put("likeCount", commentLikeService.getLikeCount(commentId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    @Operation(
            summary = "댓글 좋아요 상태 확인",
            description = "현재 사용자가 댓글에 좋아요를 했는지 확인합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> checkLike(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long commentId
    ) {
        boolean liked = commentLikeService.checkLike(userPrincipal.getId(), commentId);
        long likeCount = commentLikeService.getLikeCount(commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}

