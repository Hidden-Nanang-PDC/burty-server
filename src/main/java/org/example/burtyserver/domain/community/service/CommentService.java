package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.CommentDto;
import org.example.burtyserver.domain.community.model.entity.Comment;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.model.repository.CommentRepository;
import org.example.burtyserver.domain.community.model.repository.PostRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     */
    @Transactional
    public Comment createComment(Long userId, Long postId, CommentDto.Request request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(user)
                .post(post)
                .build();

        post.addComment(comment);
        return commentRepository.save(comment);
    }

    /**
     * 댓글 업데이트
     */
    @Transactional
    public Comment updateComment(Long userId, Long commentId, CommentDto.Request request) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        // 작성자 확인
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.update(request.getContent());
        return commentRepository.save(comment);
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) throws AccessDeniedException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));

        // 작성자 확인
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    /**
     * 게시글별 댓글 목록 조회
     */
    public List<CommentDto.Response> getCommentsByPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);
        return comments.stream()
                .map(comment -> CommentDto.Response.from(comment, userId))
                .collect(Collectors.toList());
    }

    /**
     * 사용자별 댓글 목록 조회
     */
    public List<CommentDto.Response> getCommentsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        List<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(user);
        return comments.stream()
                .map(comment -> CommentDto.Response.from(comment, userId))
                .collect(Collectors.toList());
    }

}
