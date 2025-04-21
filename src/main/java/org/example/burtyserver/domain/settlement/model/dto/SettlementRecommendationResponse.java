package org.example.burtyserver.domain.settlement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;

import java.time.LocalDateTime;

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

    private Integer age;
    private String desiredJob;
    private Integer monthlyFixedCost;

    /**
     * 엔티티를 DTO로 변환하는 정적 메서드
     * @param report 정착 리포트 엔티티
     * @return DTO 객체
     */
    public static SettlementRecommendationResponse from(SettlementReport report) {
        return SettlementRecommendationResponse.builder()
                .id(report.getId())
                .recommendedArea(report.getRecommendedArea())
                .recommendationReason(report.getRecommendationReason())
                .savingPotential(report.getSavingPotential())
                .age(report.getAge())
                .desiredJob(report.getDesiredJob())
                .monthlyFixedCost(report.getMonthlyFixedCost())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
