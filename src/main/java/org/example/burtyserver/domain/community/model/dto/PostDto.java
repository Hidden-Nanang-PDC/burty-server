package org.example.burtyserver.domain.community.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.example.burtyserver.domain.community.model.entity.Category;
import org.example.burtyserver.domain.community.model.entity.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 게시글 관련 DTO 클래스 모음
 */
public class PostDto {
    /**
     * 게시글 작성, 수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostRequest {
        private String title;
        private String content;
        private Set<Long> categoryIds;
    }

    /**
     * 게시글 목록 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private Long id;
        private String title;
        private Long authorId;
        private String authorName;
        private List<String> categoryNames;
        private int commentCount;
        private LocalDateTime createdAt;

        /**
         * 게시글 목록 응답 DTO
         */
        public static ListResponse from(Post post) {
            List<String> categoryNames = post.getCategories().stream()
                    .map(Category::getName)
                    .toList();

            return ListResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getName())
                    .categoryNames(categoryNames)
                    .commentCount(post.getComments().size())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }

    /**
     * 게시글 상세 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DetailResponse{
        private Long id;
        private String title;
        private String content;
        private Long authorId;
        private String authorName;
        private String authorImageUrl;
        private List<CategoryDto.Response> categories;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<CommentDto.Response> comments;
        private boolean isAuthor;

        public static DetailResponse from(Post post, Long currentUserId) {
            List<CommentDto.Response> commentDtos = post.getComments().stream()
                    .map(comment -> CommentDto.Response.from(comment, currentUserId))
                    .collect(Collectors.toList());
            List<CategoryDto.Response> categoryDtos = post.getCategories().stream()
                    .map(CategoryDto.Response::from)
                    .collect(Collectors.toList());

            return DetailResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getName())
                    .authorImageUrl(post.getAuthor().getProfileImageUrl())
                    .categories(categoryDtos)
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .comments(commentDtos)
                    .isAuthor(post.getAuthor().getId().equals(currentUserId))
                    .build();
        }
    }

}
