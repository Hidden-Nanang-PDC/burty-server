package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.PostDto;
import org.example.burtyserver.domain.community.model.entity.BoardCategory;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.model.repository.BoardCategoryRepository;
import org.example.burtyserver.domain.community.model.repository.PostRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 게시글 관련 비즈니스 로직을 담당하는 서비스
 */
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final UserRepository userRepository;
    private final KeywordExtractionService keywordExtractionService;

    /**
     * 게시글 생성
     */
    @Transactional
    public Post createPost(Long userId, PostDto.PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Set<BoardCategory> categories = keywordExtractionService.extractCategoriesFromContent(
                request.getContent()
        );

        Post post = Post.builder()
                .content(request.getContent())
                .author(user)
                .build();
        post.setCategories(categories);
        return postRepository.save(post);
    }

    /**
     * 게시글 업데이트
     */
    @Transactional
    public Post updatePost(Long userId, Long postId, PostDto.PostRequest request) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID : " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID : "+ postId));

        if (!post.getAuthor().getId().equals(userId)){
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        Set<BoardCategory> categories = keywordExtractionService.extractCategoriesFromContent(
                request.getContent()
        );
        post.setCategories(categories);
        return postRepository.save(post);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deletePost(Long userId, Long postId) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        // 작성자 확인
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional
    public PostDto.DetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다. ID: " + postId));

        post.incrementViewCount();
        postRepository.save(post);
        User currentUser;
        currentUser = userRepository.findById(userId).orElse(null);

        return PostDto.DetailResponse.from(post, currentUser);
    }

    /**
     * 게시글 목록 조회
     */
    public Page<PostDto.ListResponse> getPosts(Pageable pageable, Long userId) {
        Page<Post> posts = postRepository.findAll(pageable);

        User currentUser;
        if (userId != null) {
            currentUser = userRepository.findById(userId).orElse(null);
        } else {
            currentUser = null;
        }

        return posts.map(post -> PostDto.ListResponse.from(post, currentUser));
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    public Page<PostDto.ListResponse> getPostsByCategory(Long categoryId, Long currentUserId, Pageable pageable) {
        BoardCategory boardCategory = boardCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        // pageable에 이미 정렬 정보가 포함되어 있으므로, 커스텀 정렬 로직 필요 없음
        Page<Post> posts = postRepository.findByCategoryId(categoryId, pageable);

        User currentUser;
        if (currentUserId != null) {
            currentUser = userRepository.findById(currentUserId).orElse(null);
        } else {
            currentUser = null;
        }

        return posts.map(post -> PostDto.ListResponse.from(post, currentUser));
    }

    /**
     * 사용자별 게시글 목록 조회
     */
    public List<PostDto.ListResponse> getPostsByUser(Long authorId, Long currentUserId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + authorId));
        // 현재 사용자 조회
        User currentUser;
        if (currentUserId != null) {
            // 조회하는 사용자와 현재 사용자가 같은 경우 재사용
            if (authorId.equals(currentUserId)) {
                currentUser = author;
            } else {
                currentUser = userRepository.findById(currentUserId).orElse(null);
            }
        } else {
            currentUser = null;
        }

        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(author);
        return posts.stream()
                .map(post -> PostDto.ListResponse.from(post, currentUser))
                .collect(Collectors.toList());
    }

}
