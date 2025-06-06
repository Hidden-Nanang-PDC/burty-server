package org.example.burtyserver.domain.community.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 게시판 카테고리 엔티티
 */
@Entity
@Table(name = "community_categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<Post> posts = new HashSet<>();

    public void update(String name){
        this.name = name;
    }
}
