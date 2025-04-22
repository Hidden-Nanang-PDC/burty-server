package org.example.burtyserver.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.CategoryDto;
import org.example.burtyserver.domain.community.model.entity.Category;
import org.example.burtyserver.domain.community.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 게시판 카테고리 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/community/categories")
@RequiredArgsConstructor
@Tag(name = "커뮤니티 카테고리", description = "게시판 카테고리 관련 API")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 카테고리 생성 API
     */
    @PostMapping
    @Operation(
            summary = "카테고리 생성",
            description = "새로운 게시판 카테고리를 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> createCategory(@RequestBody CategoryDto.Request request) {
        Category category = categoryService.createCategory(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "카테고리가 성공적으로 생성되었습니다.");
        response.put("categoryId", category.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리 수정 API
     */
    @PutMapping("/{categoryId}")
    @Operation(
            summary = "카테고리 수정",
            description = "기존 카테고리를 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDto.Request request
    ) {
        Category category = categoryService.updateCategory(categoryId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "카테고리가 성공적으로 수정되었습니다.");
        response.put("categoryId", category.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리 삭제 API (관리자 전용)
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리를 삭제합니다 (관리자 전용)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "카테고리가 성공적으로 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 카테고리 목록 조회 API
     */
    @GetMapping
    @Operation(
            summary = "카테고리 목록 조회",
            description = "모든 카테고리 목록을 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<CategoryDto.Response>> getAllCategories() {
        List<CategoryDto.Response> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 카테고리 상세 조회 API
     */
    @GetMapping("/{categoryId}")
    @Operation(
            summary = "카테고리 상세 조회",
            description = "특정 카테고리의 상세 정보를 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<CategoryDto.Response> getCategoryById(@PathVariable Long categoryId) {
        CategoryDto.Response category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

}
