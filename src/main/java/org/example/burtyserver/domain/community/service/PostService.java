package org.example.burtyserver.domain.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.dto.PostDto;
import org.example.burtyserver.domain.community.model.entity.Category;
import org.example.burtyserver.domain.community.model.entity.Post;
import org.example.burtyserver.domain.community.model.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 생성
     */
    @Transactional
    public Post createPost(Long userId, PostDto.PostRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Set<Category> categories = null;
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            categories = request.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID : "+ categoryId)))
                    .collect(Collectors.toSet());
        }

        Post post = Post.builder()
                .title(request.getTitle())
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

        Set<Category> categories = new HashSet<>();
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()){
            categories = request.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID : "+categoryId)))
                    .collect(Collectors.toSet());
        }
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

        postRepository.save(post);

        return PostDto.DetailResponse.from(post, userId);
    }

    /**
     * 게시글 목록 조회
     */
    public Page<PostDto.ListResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        return posts.map(PostDto.ListResponse::from);
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    public Page<PostDto.ListResponse> getPostsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다. ID: " + categoryId));

        Page<Post> posts = postRepository.findByCategoryIdOrderByCreatedAtDesc(category.getId(), pageable);
        return posts.map(PostDto.ListResponse::from);
    }

    /**
     * 사용자별 게시글 목록 조회
     */
    public List<PostDto.ListResponse> getPostsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));

        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user);
        return posts.stream()
                .map(PostDto.ListResponse::from)
                .collect(Collectors.toList());
    }
}
