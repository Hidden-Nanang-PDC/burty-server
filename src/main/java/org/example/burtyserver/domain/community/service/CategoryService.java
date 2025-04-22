package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.CategoryDto;
import org.example.burtyserver.domain.community.model.entity.Category;
import org.example.burtyserver.domain.community.model.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public Category createCategory(CategoryDto.Request request) {
        // 카테고리명 중복 확인
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        return categoryRepository.save(category);
    }

    /**
     * 카테고리 업데이트
     */
    @Transactional
    public Category updateCategory(Long categoryId, CategoryDto.Request request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        // 카테고리명 중복 확인 (다른 카테고리와 중복되는지)
        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }

        category.update(request.getName());
        return categoryRepository.save(category);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        // 게시글이 있는 카테고리는 삭제할 수 없음
        if (!category.getPosts().isEmpty()) {
            throw new IllegalStateException("게시글이 있는 카테고리는 삭제할 수 없습니다.");
        }

        categoryRepository.delete(category);
    }

    /**
     * 카테고리 목록 조회
     */
    public List<CategoryDto.Response> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 상세 조회
     */
    public CategoryDto.Response getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        return CategoryDto.Response.from(category);
    }
}
