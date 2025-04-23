package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.Comment;
import org.example.burtyserver.domain.community.model.entity.CommentLike;
import org.example.burtyserver.domain.community.model.repository.CommentLikeRepository;
import org.example.burtyserver.domain.community.model.repository.CommentRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * 댓글 좋아요 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    /**
     * 댓글 좋아요 추가
     */
    @Transactional
    public boolean addLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            return false; // 이미 좋아요가 존재
        }

        CommentLike commentLike = comment.addLike(user);
        commentLikeRepository.save(commentLike);
        return true;
    }

    /**
     * 댓글 좋아요 취소
     */
    @Transactional
    public boolean removeLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        CommentLike commentLike = commentLikeRepository.findByCommentAndUser(comment, user).orElse(null);

        if (commentLike == null) return false;

        comment.removeLike(user);
        commentLikeRepository.delete(commentLike);
        return true;
    }

    /**
     * 댓글 좋아요 여부 확인
     */
    public boolean checkLike(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        return commentLikeRepository.existsByCommentAndUser(comment, user);
    }

    /**
     * 댓글 좋아요 수 조회
     */
    public long getLikeCount(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        return commentLikeRepository.countByComment(comment);
    }
}
