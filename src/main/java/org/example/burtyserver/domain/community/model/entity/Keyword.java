package org.example.burtyserver.domain.community.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "community_keywords")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String word; //서울, 20대, IT개발 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private KeywordType category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapped_category_id")
    private BoardCategory mappedCategory; //키워드가 발견되면 매핑할 실게 게시판 카테고리
}
