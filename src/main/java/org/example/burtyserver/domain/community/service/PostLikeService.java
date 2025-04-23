package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.UniqueConstraint;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.model.entity.PostLike;
import org.example.burtyserver.domain.community.model.repository.PostLikeRepository;
import org.example.burtyserver.domain.community.model.repository.PostRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * 게시글 좋아요 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    /**
     * 게시글 좋아요 추가
     */
    @Transactional
    public boolean addLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾ㅇ르 수 없습니다. ID :" +postId));

        if (postLikeRepository.existsByPostAndUser(post, user)){
            return false; //이미 좋아요가 존재
        }

        PostLike postLike = post.addLike(user);
        postLikeRepository.save(postLike);
        return true;
    }

    /**
     * 게시글 좋아요 취소
     */
    @Transactional
    public boolean removeLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾ㅇ르 수 없습니다. ID :" +postId));

        PostLike postLike = postLikeRepository.findByPostAndUser(post, user).orElse(null);

        if (postLike == null) return false;

        post.removeLike(user);
        postLikeRepository.delete(postLike);
        return true;
    }

    /**
     * 게시글 좋아요 여부 확인
     */
    public boolean checkLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾ㅇ르 수 없습니다. ID :" +postId));

        return postLikeRepository.existsByPostAndUser(post, user);
    }

    /**
     * 게시글 좋아요 수 조회
     */
    public long getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        return postLikeRepository.countByPost(post);
    }
}
