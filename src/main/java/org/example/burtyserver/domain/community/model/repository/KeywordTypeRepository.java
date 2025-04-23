package org.example.burtyserver.domain.community.model.repository;

import org.example.burtyserver.domain.community.model.entity.KeywordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KeywordTypeRepository extends JpaRepository<KeywordType, Long> {
    Optional<KeywordType> findByName(String name);
}
