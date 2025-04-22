package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.Category;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 데이터 접근 인터페이스
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 전체 게시글 목록 페이징 조회 (최신순)
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 사용자별 게시글 목록 조회
     */
    List<Post> findByAuthorOrderByCreatedAtDesc(User user);

    /**
     * 카테고리별 게시글 목록 페이징 조회
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN p.categories c WHERE c.id = :categoryId ORDER BY p.createdAt DESC")
    Page<Post> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId, Pageable pageable);


}
