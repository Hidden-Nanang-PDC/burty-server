package org.example.burtyserver.domain.community.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.Category;

/**
 * 카테고리 관련 DTO 클래스 모음
 */
public class CategoryDto {
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

        public static Response from(Category category) {
            return Response.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .postCount(category.getPosts().size())
                    .build();
        }
    }
}
