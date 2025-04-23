package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 카테고리 데이터 접근 인터페이스
 */
@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {
    /**
     * 카테고리 명으로 카테고리 조회
     */
    Optional<BoardCategory> findByName(String name);

    /**
     * 카테고리명 중복 확인
     */
    boolean existsByName(String name);
}
