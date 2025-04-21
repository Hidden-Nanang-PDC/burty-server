package org.example.burtyserver.domain.settlement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 정착 추천 결과를 반환하기 위한 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRecommendationResponse {
    private Long id;
    private String recommendedArea;
    private String recommendationReason;
    private String savingPotential;
    private LocalDateTime createdAt;
    private List<PolicyDto> policies;

    private Integer age;
    private String desiredJob;
    private Integer monthlyFixedCost;

    /**
     * 엔티티를 DTO로 변환하는 정적 메서드
     * @param report 정착 리포트 엔티티
     * @return DTO 객체
     */
    public static SettlementRecommendationResponse from(SettlementReport report) {
        List<PolicyDto> policyDtos = report.getPolicies() != null ?
                report.getPolicies().stream()
                        .map(policy -> new PolicyDto(
                                policy.getPolicyName(),
                                policy.getPolicyDescription(),
                                policy.getPolicyUrl()
                        )).toList() :
                new ArrayList<>();

        return SettlementRecommendationResponse.builder()
                .id(report.getId())
                .recommendedArea(report.getRecommendedArea())
                .recommendationReason(report.getRecommendationReason())
                .savingPotential(report.getSavingPotential())
                .policies(policyDtos)
                .age(report.getAge())
                .desiredJob(report.getDesiredJob())
                .monthlyFixedCost(report.getMonthlyFixedCost())
                .createdAt(report.getCreatedAt())
                .build();
    }

    /**
     * 정착 정보를 담는 내부 DTO 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class PolicyDto{
        private String name;
        private String description;
        private String url;
    }
}
