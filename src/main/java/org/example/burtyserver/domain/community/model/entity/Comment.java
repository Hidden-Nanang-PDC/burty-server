package org.example.burtyserver.domain.community.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.burtyserver.domain.user.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 게시글 댓글 엔티티
 */
@Entity
@Table(name = "community_comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentLike> likes = new HashSet<>();

    public void update(String content){
        this.content = content;
    }

    public int getLikeCount() {
        return likes.size();
    }

    public boolean isLikedByUser(User user) {
        return likes.stream()
                .anyMatch(like -> like.getUser().getId().equals(user.getId()));
    }

    public CommentLike addLike(User user) {
        CommentLike commentLike = CommentLike.builder()
                .comment(this)
                .user(user)
                .build();
        this.likes.add(commentLike);
        return commentLike;
    }

    public void removeLike(User user) {
        this.likes.removeIf(like -> like.getUser().getId().equals(user.getId()));
    }

}
