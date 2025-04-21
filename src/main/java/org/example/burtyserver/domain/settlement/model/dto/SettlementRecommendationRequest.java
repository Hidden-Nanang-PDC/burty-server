package org.example.burtyserver.domain.settlement.model.dto;

import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import lombok.Setter;

/**
 * 정착 추천 요청을 위한 DTO 클래스
 */
@Getter
@Setter
public class SettlementRecommendationRequest {
    private Integer age;
    private String desiredJob;
    private Integer monthlyFixedCost;
}
