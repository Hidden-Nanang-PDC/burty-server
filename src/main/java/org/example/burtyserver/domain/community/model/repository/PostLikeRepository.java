package org.example.burtyserver.domain.community.model.repository;


import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.model.entity.PostLike;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 특정 게시글에 대한 특정 사용자의 좋아요 찾기
     */
    Optional<PostLike> findByPostAndUser(Post post,User user);

    /**
     * 특정 게시글에 대한 특정 사용자의 좋아요 존재 여부
     */
    boolean existsByPostAndUser(Post post, User user);

    /**
     * 특정 게시글의 좋아요 수 조회
     */
    long countByPost(Post post);

    /**
     * 특정 사용자가 좋아요한 게시글 수 조회
     */
    long countByUser(User user);
}
