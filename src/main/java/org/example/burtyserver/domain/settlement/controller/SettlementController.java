package org.example.burtyserver.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationRequest;
import org.example.burtyserver.domain.settlement.service.SettlementService;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Tag(name = "정착지 추천", description = "청년 정착지 추천 관련 API")
public class SettlementController {
    private final SettlementService settlementService;

    @PostMapping("/recommend")
    @Operation(
            summary = "정착지 추천 요청",
            description = "사용자의 나이, 희망 직무, 월 고정비를 입력받아 최적의 정착지를 추천합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "정착지 추천 성공")
    public ResponseEntity<String> recommendSettlement(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody SettlementRecommendationRequest request
            ) throws IOException{
        String response = settlementService.recommendSettlement(userPrincipal.getId(), request);

        return ResponseEntity.ok(response);
    }
}
