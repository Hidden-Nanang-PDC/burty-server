package org.example.burtyserver.domain.settlement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.example.burtyserver.domain.settlement.model.dto.SettlementListResponse;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationRequest;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationResponse;
import org.example.burtyserver.domain.settlement.model.entity.SettlementPolicy;
import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;
import org.example.burtyserver.domain.settlement.model.repository.SettlementReportRepository;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 정착지 추천 비즈니스 로직을 담당하는 서비스
 */
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
    public SettlementRecommendationResponse recommendSettlement(Long userId, SettlementRecommendationRequest request) throws IOException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID : " + userId));

        // Gemini API 호출
        String apiResponse = geminiAPIService.getSettlementRecommendation(
                request.getAge(),
                request.getDesiredJob(),
                request.getMonthlyFixedCost()
        );

        System.out.println("apiResponse : \n" +apiResponse);

        // API 응답 파싱
        SettlementReport report = parseAPIResponseAndCreateReport(apiResponse, user, request);
        settlementReportRepository.save(report);

        return SettlementRecommendationResponse.from(report);
    }

    /**
     * API 응답을 파싱하여 정착 리포트 엔티티를 생성합니다.
     *
     * @param apiResponse API 응답 JSON
     * @param user 사용자 엔티티
     * @param request 요청 DTO
     * @return 생성된 정착 리포트 엔티티
     * @throws IOException JSON 파싱 중 오류 발생 시
     */
    private SettlementReport parseAPIResponseAndCreateReport(String apiResponse, User user, SettlementRecommendationRequest request) throws IOException{
        JsonNode responseNode = objectMapper.readTree(apiResponse);
        JsonNode candidatesNode = responseNode.path("candidates");
        if (!candidatesNode.isArray() || candidatesNode.isEmpty()) {
            throw new IOException("API 응답에 candidates 배열이 없거나 비어있습니다.");
        }

        JsonNode contentNode = candidatesNode.get(0).path("content");
        JsonNode partsNode = contentNode.path("parts");
        if (!partsNode.isArray() || partsNode.isEmpty()) {
            throw new IOException("API 응답에 parts 배열이 없거나 비어 있습니다.");
        }

        String textContent = partsNode.get(0).path("text").asText();

        if (textContent.startsWith("```json")) {
            textContent = textContent.substring(8, textContent.lastIndexOf("```")).trim();
        } else if (textContent.contains("```")) {
            // 다른 형태의 코드 블록일 경우
            int startIndex = textContent.indexOf("```") + 3;
            int endIndex = textContent.lastIndexOf("```");
            if (startIndex < endIndex) {
                // 첫 번째 ```이후부터 마지막 ```까지의 문자열
                textContent = textContent.substring(
                        textContent.indexOf("\n", startIndex) + 1,
                        endIndex
                ).trim();
            }
        }

        JsonNode jsonNode = objectMapper.readTree(textContent);

        // 기본 정착 리포트 생정
        SettlementReport report = SettlementReport.builder()
                .user(user)
                .age(request.getAge())
                .desiredJob(request.getDesiredJob())
                .monthlyFixedCost(request.getMonthlyFixedCost())
                .recommendedArea(jsonNode.path("recommendedArea").asText())
                .recommendationReason(jsonNode.path("recommendationReason").asText())
                .savingPotential(jsonNode.path("savingPotential").asText())
                .policies(new ArrayList<>())
                .build();

        // 정책 정보 파싱 및 추가
        JsonNode policiesNode = jsonNode.path("policies");
        if (policiesNode.isArray()){
            for (JsonNode policyNode :  policiesNode) {
                SettlementPolicy policy = SettlementPolicy.builder()
                        .policyName(policyNode.path("name").asText())
                        .policyDescription(policyNode.path("description").asText())
                        .policyUrl(policyNode.path("url").asText(""))
                        .build();
                report.addPolicy(policy);
            }
        }

        log.debug("파싱된 JSON: {}", jsonNode);
        log.debug("생성된 리포트: {}", report);
        return report;
    }

    /**
     * 사용자의 모든 정착 리포트를 조회합니다.
     * @param userId 사용자 ID
     * @return 정착 리포트 응답 DTO 목록
     */
    public List<SettlementListResponse> getUserReports(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        List<SettlementReport> reports = settlementReportRepository.findByUserOrderByCreatedAtDesc(user);
        List<SettlementListResponse> responses = new ArrayList<>();

        for (SettlementReport report : reports) {
            responses.add(SettlementListResponse.from(report));
        }
        return responses;
    }

    /**
     * 특정 정착 리포트를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param reportId 리포트 ID
     * @return 정착 리포트 응답 DTO
     */
    public SettlementRecommendationResponse getReportById(Long userId, Long reportId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자ㅏ를 찾을 수 없습니다. ID: " +userId));

        SettlementReport report = settlementReportRepository.findByUserAndId(user, reportId)
                .orElseThrow(() -> new RuntimeException("리포트를 찾을 수 없습니다. ID : "+ reportId));

        return SettlementRecommendationResponse.from(report);
    }
}
