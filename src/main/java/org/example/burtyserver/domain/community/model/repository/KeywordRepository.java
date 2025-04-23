package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.Keyword;
import org.example.burtyserver.domain.community.model.entity.KeywordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByCategory(KeywordType category);
    List<Keyword> findAll();
}
