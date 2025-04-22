package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.Comment;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글별 댓글 목록 조회 (생성일시 순)
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * 사용자가 작성한 댓글 목록 조회
     */
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);

    /**
     * 특정 사용자와 댓글 ID로 댓글 조회
     */
    Optional<Comment> findByIdAndAuthor(Long id, User author);
}
