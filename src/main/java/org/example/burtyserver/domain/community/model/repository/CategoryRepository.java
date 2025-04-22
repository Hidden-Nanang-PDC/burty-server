package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 카테고리 데이터 접근 인터페이스
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * 카테고리 명으로 카테고리 조회
     */
    Optional<Category> findByName(String name);

    /**
     * 카테고리명 중복 확인
     */
    boolean existsByName(String name);
}
