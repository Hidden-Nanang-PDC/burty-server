package org.example.burtyserver.domain.community.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.BoardCategory;

/**
 * 카테고리 관련 DTO 클래스 모음
 */
public class BoardCategoryDto {
    /**
     * 카테고리 생성, 수정 요청 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String name;
    }

    /**
     * 카테고리 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private int postCount;

        public static Response from(BoardCategory boardCategory) {
            return Response.builder()
                    .id(boardCategory.getId())
                    .name(boardCategory.getName())
                    .postCount(boardCategory.getPosts().size())
                    .build();
        }
    }
}
