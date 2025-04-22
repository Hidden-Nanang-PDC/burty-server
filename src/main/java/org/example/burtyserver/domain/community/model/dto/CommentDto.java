package org.example.burtyserver.domain.community.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.Comment;

import java.time.LocalDateTime;

/**
 *  댓글 관련 DTO 클래스 모음
 */
public class CommentDto {
    /**
     * 댓글 작성, 수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String content;
    }

    /**
     * 댓글 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long id;
        private String content;
        private Long authorId;
        private String authorName;
        private String authorImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean isAuthor;

        public static Response from(Comment comment, Long currentUserId) {
            return Response.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .authorId(comment.getAuthor().getId())
                    .authorName(comment.getAuthor().getName())
                    .authorImageUrl(comment.getAuthor().getProfileImageUrl())
                    .createdAt(comment.getCreatedAt())
                    .updatedAt(comment.getUpdatedAt())
                    .isAuthor(comment.getAuthor().getId().equals(currentUserId))
                    .build();
        }
    }
}
