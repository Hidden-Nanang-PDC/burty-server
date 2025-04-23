package org.example.burtyserver.domain.community.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.user.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 댓글 좋아요 엔티티
 */
@Entity
@Table(name = "community_comment_likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"comment_id", "user_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
