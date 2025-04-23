package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.KeywordCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordCategoryRepository extends JpaRepository<KeywordCategory, Long> {
    Optional<KeywordCategory> findByName(String name);
}
