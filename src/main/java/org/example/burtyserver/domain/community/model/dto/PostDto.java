package org.example.burtyserver.domain.community.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.BoardCategory;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.user.model.entity.User;

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
        private String content;
        private Long authorId;
        private String authorName;
        private List<String> categoryNames;
        private int commentCount;
        private LocalDateTime createdAt;
        private int likeCount;
        private boolean liked; // 현재 사용자가 좋아요를 눌렀는지 여부
        private Long viewCount;

        /**
         * 게시글 목록 응답 DTO
         */
        public static ListResponse from(Post post, User currentUser) {
            List<String> categoryNames = post.getCategories().stream()
                    .map(BoardCategory::getName)
                    .toList();

            return ListResponse.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getName())
                    .categoryNames(categoryNames)
                    .commentCount(post.getComments().size())
                    .createdAt(post.getCreatedAt())
                    .likeCount(post.getLikeCount())
                    .liked(currentUser != null && post.isLikedByUser(currentUser))
                    .viewCount(post.getViewCount())
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
        private String content;
        private Long authorId;
        private String authorName;
        private String authorImageUrl;
        private List<BoardCategoryDto.Response> categories;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<CommentDto.Response> comments;
        private boolean isAuthor;
        private int likeCount;
        private boolean liked;
        private Long viewCount;

        public static DetailResponse from(Post post, User currentUser) {
            List<CommentDto.Response> commentDtos = post.getComments().stream()
                    .map(comment -> CommentDto.Response.from(comment, currentUser))
                    .collect(Collectors.toList());
            List<BoardCategoryDto.Response> categoryDtos = post.getCategories().stream()
                    .map(BoardCategoryDto.Response::from)
                    .collect(Collectors.toList());

            return DetailResponse.builder()
                    .id(post.getId())
                    .content(post.getContent())
                    .authorId(post.getAuthor().getId())
                    .authorName(post.getAuthor().getName())
                    .authorImageUrl(post.getAuthor().getProfileImageUrl())
                    .categories(categoryDtos)
                    .createdAt(post.getCreatedAt())
                    .updatedAt(post.getUpdatedAt())
                    .comments(commentDtos)
                    .isAuthor(currentUser != null && post.getAuthor().getId().equals(currentUser.getId()))
                    .likeCount(post.getLikeCount())
                    .liked(currentUser != null && post.isLikedByUser(currentUser))
                    .viewCount(post.getViewCount())
                    .build();
        }
    }

}
