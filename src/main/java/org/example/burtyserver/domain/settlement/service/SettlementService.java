package org.example.burtyserver.domain.settlement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationRequest;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationResponse;
import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;
import org.example.burtyserver.domain.settlement.model.repository.SettlementReportRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {
    private final GeminiAPIService geminiAPIService;
    private final SettlementReportRepository settlementReportRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * 사용자 입력을 바탕으로 정착지를 추천하고 결과를 저장합니다.
     *
     * @param userId 사용자 ID
     * @param request 정착지 추천 요청 DTO
     * @return 정착지 추천 응답 DTO
     * @throws IOException API 호출 중 오류 발생 시
     */
    @Transactional
    public String recommendSettlement(Long userId, SettlementRecommendationRequest request) throws IOException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID : " + userId));

        // Gemini API 호출
        String apiResponse = geminiAPIService.getSettlementRecommendation(
                request.getAge(),
                request.getDesiredJob(),
                request.getMonthlyFixedCost()
        );

        System.out.println(apiResponse);
        return apiResponse;
    }

}
