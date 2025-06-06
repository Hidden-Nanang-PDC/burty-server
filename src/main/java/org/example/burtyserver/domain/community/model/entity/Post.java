package org.example.burtyserver.domain.community.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.burtyserver.domain.user.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 커뮤니티 게시글 엔티티
 */
@Entity
@Table(name = "community_posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    @ManyToMany
    @JoinTable(
            name = "post_categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<BoardCategory> categories = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostLike> likes = new HashSet<>();


    public void update(String content, Set<BoardCategory> categories){
        this.content = content;
        this.categories = categories;
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void addCategory(BoardCategory boardCategory) {
        this.categories.add(boardCategory);
    }

    public void removeCategory(BoardCategory boardCategory){
        this.categories.remove(boardCategory);
    }

    public int getLikeCount() {
        return likes.size();
    }

    public boolean isLikedByUser(User user){
        return likes.stream()
                .anyMatch(like -> like.getUser().getId().equals(user.getId()));
    }

    public PostLike addLike(User user){
        PostLike postLike = PostLike.builder()
                .post(this)
                .user(user)
                .build();
        this.likes.add(postLike);
        return postLike;
    }

    public void removeLike(User user) {
        this.likes.removeIf(like -> like.getUser().getId().equals(user.getId()));
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null) ? 1L : this.viewCount + 1L;
    }
}
