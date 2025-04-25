package org.example.burtyserver.domain.settlement.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;

import java.time.LocalDateTime;

/**
 * 정착 리포트 목록 조회용 간소화된 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementListResponse {
    private Long id;
    private String recommendedArea;
    private String desiredJob;
    private LocalDateTime createdAt;

    public static SettlementListResponse from (SettlementReport report) {
        return SettlementListResponse.builder()
                .id(report.getId())
                .recommendedArea(report.getRecommendedArea())
                .desiredJob(report.getDesiredJob())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
