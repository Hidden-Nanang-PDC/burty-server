package org.example.burtyserver.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.CommentDto;
import org.example.burtyserver.domain.community.model.dto.PostDto;
import org.example.burtyserver.domain.community.model.entity.Comment;
import org.example.burtyserver.domain.community.service.CommentService;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 댓글 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/community/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 댓글", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    /**
     * 댓글 작성 API
     */
    @PostMapping
    @Operation(
            summary = "댓글 작성",
            description = "게시글에 새로운 댓글을 작성합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> createComment(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestBody CommentDto.Request request
    ) {
        Comment comment = commentService.createComment(userPrincipal.getId(), postId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "댓글이 성공적으로 등록되었습니다.");
        response.put("commentId", comment.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정 API
     */
    @PutMapping("/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "기존 댓글을 수정합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> updateComment(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentDto.Request request
    ) throws AccessDeniedException {
        Comment comment = commentService.updateComment(userPrincipal.getId(), commentId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "댓글이 성공적으로 수정되었습니다.");
        response.put("commentId", comment.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제 API
     */
    @DeleteMapping("/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "댓글을 삭제합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> deleteComment(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) throws AccessDeniedException {
        commentService.deleteComment(userPrincipal.getId(), commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "댓글이 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글별 댓글 목록 조회 API
     */
    @GetMapping
    @Operation(
            summary = "게시글별 댓글 목록 조회",
            description = "특정 게시글의 댓글 목록을 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPost(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId
    ) {
        List<CommentDto.Response> comments = commentService.getCommentsByPost(postId, userPrincipal.getId());
        return ResponseEntity.ok(comments);
    }

    /**
     * 내가 댓글을 작성한 게시글 목록 조회 API
     */
    @GetMapping("/commented-by-me")
    @Operation(
            summary = "내가 댓글을 작성한 게시글 목록 조회",
            description = "현재 로그인한 사용자가 댓글을 작성한 게시글 목록을 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PostDto.ListResponse>> getPostsCommentedByMe(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        List<PostDto.ListResponse> posts = commentService.getPostsCommentedByUser(userPrincipal.getId());
        return ResponseEntity.ok(posts);
    }
}
