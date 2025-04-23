package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.BoardCategoryDto;
import org.example.burtyserver.domain.community.model.entity.BoardCategory;
import org.example.burtyserver.domain.community.model.repository.BoardCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class BoardCategoryService {
    private final BoardCategoryRepository boardCategoryRepository;

    /**
     * 카테고리 생성
     */
    @Transactional
    public BoardCategory createCategory(BoardCategoryDto.Request request) {
        // 카테고리명 중복 확인
        if (boardCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }

        BoardCategory boardCategory = BoardCategory.builder()
                .name(request.getName())
                .build();

        return boardCategoryRepository.save(boardCategory);
    }

    /**
     * 카테고리 업데이트
     */
    @Transactional
    public BoardCategory updateCategory(Long categoryId, BoardCategoryDto.Request request) {
        BoardCategory boardCategory = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        // 카테고리명 중복 확인 (다른 카테고리와 중복되는지)
        if (!boardCategory.getName().equals(request.getName()) &&
                boardCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 이름입니다: " + request.getName());
        }

        boardCategory.update(request.getName());
        return boardCategoryRepository.save(boardCategory);
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        BoardCategory boardCategory = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        // 게시글이 있는 카테고리는 삭제할 수 없음
        if (!boardCategory.getPosts().isEmpty()) {
            throw new IllegalStateException("게시글이 있는 카테고리는 삭제할 수 없습니다.");
        }

        boardCategoryRepository.delete(boardCategory);
    }

    /**
     * 카테고리 목록 조회
     */
    public List<BoardCategoryDto.Response> getAllCategories() {
        List<BoardCategory> categories = boardCategoryRepository.findAll();
        return categories.stream()
                .map(BoardCategoryDto.Response::from)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 상세 조회
     */
    public BoardCategoryDto.Response getCategoryById(Long categoryId) {
        BoardCategory boardCategory = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        return BoardCategoryDto.Response.from(boardCategory);
    }
}
