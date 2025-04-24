package org.example.burtyserver.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.PostDto;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.service.PostService;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 게시글", description = "게시글 관련 API")
public class PostController {
    private final PostService postService;

    /**
     * 게시글 작성 API
     */
    @PostMapping
    @Operation(
            summary = "게시글 작성",
            description = "새로운 게시글을 작성합니다. categoryIds는 삭제하고 요청 가능",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> createPost(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody PostDto.PostRequest request
            ) {
        Post post = postService.createPost(userPrincipal.getId(), request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "게시글이 성공적으로 등록되었습니다.");
        response.put("postId", post.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정 API
     */
    @PutMapping("/{postId}")
    @Operation(
            summary = "게시글 수정",
            description = "기존 게시글을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> updatePost(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @RequestBody PostDto.PostRequest request
    ) throws AccessDeniedException {
        Post post = postService.updatePost(userPrincipal.getId(), postId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "게시글이 성공적으로 등록되었습니다.");
        response.put("postId", post.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 삭제 API
     */
    @DeleteMapping("/{postId}")
    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다. (현재 로그인한 사용자가 작성한 글만 삭제 가능)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> deletePost(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId
    ) throws AccessDeniedException {
        postService.deletePost(userPrincipal.getId(), postId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "게시글이 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회 API
     */
    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 상세 정보와 댓글을 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PostDto.DetailResponse> getPostDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long postId
    ) {
        PostDto.DetailResponse post = postService.getPostDetail(postId, userPrincipal.getId());
        return ResponseEntity.ok(post);
    }

    /**
     * 게시글 목록 조회 API
     */
    @GetMapping
    @Operation(
            summary = "게시글 목록 조회",
            description = "게시글 목록을 조회합니다. sort 파라미터를 통해 정렬 방식을 지정할 수 있습니다(예: sort=createdAt,desc 또는 sort=viewCount,desc). sort 값이 하나일 경우 []를 삭제해주세요.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Page<PostDto.ListResponse>> getPosts(
            @CurrentUser UserPrincipal userPrincipal,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostDto.ListResponse> posts = postService.getPosts(pageable, userPrincipal.getId());
        return ResponseEntity.ok(posts);
    }

    /**
     * 카테고리별 게시글 목록 조회 API
     */
    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "카테고리별 게시글 목록 조회",
            description = "특정 카테고리의 게시글 목록을 조회합니다. sort 파라미터를 통해 정렬 방식을 지정할 수 있습니다(예: sort=createdAt,desc 또는 sort=viewCount,desc). " +
                    "\nsort 값이 하나일 경우 []를 삭제해주세요.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Page<PostDto.ListResponse>> getPostsByCategory(
            @PathVariable Long categoryId,
            @CurrentUser UserPrincipal userPrincipal,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<PostDto.ListResponse> posts = postService.getPostsByCategory(categoryId, userPrincipal.getId(), pageable);
        return ResponseEntity.ok(posts);
    }

    /**
     * 내가 작성한 게시글 목록 조회 API
     */
    @GetMapping("/my")
    @Operation(
            summary = "내가 작성한 게시글 목록 조회",
            description = "현재 로그인한 사용자가 작성한 게시글 목록을 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<PostDto.ListResponse>> getMyPosts(
            @CurrentUser UserPrincipal userPrincipal
    ) {
        List<PostDto.ListResponse> posts = postService.getPostsByUser(userPrincipal.getId(), userPrincipal.getId());
        return ResponseEntity.ok(posts);
    }

}
