package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.Comment;
import org.example.burtyserver.domain.community.model.entity.CommentLike;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    /**
     * 특정 댓글에 대한 특정 사용자의 좋아요 찾기
     */
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);

    /**
     * 특정 댓글에 대한 특정 사용자의 좋아요 존재 여부
     */
    boolean existsByCommentAndUser(Comment comment, User user);

    /**
     * 특정 댓글의 좋아요 수 조회
     */
    long countByComment(Comment comment);

    /**
     * 특정 사용자가 좋아요한 댓글 목록 조회
     */
    @Query("SELECT cl.comment FROM CommentLike cl WHERE cl.user.id = :userId ORDER BY cl.createdAt DESC")
    Page<Comment> findCommentsLikedByUser(@Param("userId") Long userId, Pageable pageable);

}
