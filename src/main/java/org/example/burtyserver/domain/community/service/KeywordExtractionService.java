package org.example.burtyserver.domain.community.service;

import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.community.model.entity.BoardCategory;
import org.example.burtyserver.domain.community.model.entity.Keyword;
import org.example.burtyserver.domain.community.model.repository.KeywordRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeywordExtractionService {
    private final KeywordRepository keywordRepository;

    /**
     * 게시글 내용에서 등록된 키워드를 추출하려 매핑된 카테고리 목록 반환
     */
    public Set<BoardCategory> extractCategoriesFromContent(String content) {
        Set<BoardCategory> categories = new HashSet<>();

        List<Keyword> allKeywords = keywordRepository.findAll();
        String fullText = content.toLowerCase();

        for (Keyword keyword : allKeywords) {
            String keywordText = keyword.getWord().toLowerCase();
            if (fullText.matches(".*\\b" + keywordText + ".*") || fullText.contains(keywordText)) {
                if (keyword.getMappedCategory() != null) {
                    categories.add(keyword.getMappedCategory());
                }
            }
        }
        return categories;
    }
}
