package org.example.burtyserver.domain.settlement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationRequest;
import org.example.burtyserver.domain.settlement.model.dto.SettlementRecommendationResponse;
import org.example.burtyserver.domain.settlement.service.SettlementService;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.global.security.CurrentUser;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 정착지 추천 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
@Tag(name = "정착지 추천", description = "청년 정착지 추천 관련 API")
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 정착지 추천 요청 API
     *
     * @param userPrincipal 인증된 사용자 정보
     * @param request 정착지 추천 요청 DTO
     * @return 정착지 추천 결과
     * @throws IOException API 호출 중 오류 발생 시
     */
    @PostMapping("/recommend")
    @Operation(
            summary = "정착지 추천 요청",
            description = "사용자의 나이, 희망 직무, 월 고정비를 입력받아 최적의 정착지를 추천합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "정착지 추천 성공")
    public ResponseEntity<SettlementRecommendationResponse> recommendSettlement(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody SettlementRecommendationRequest request
            ) throws IOException{
        SettlementRecommendationResponse response = settlementService.recommendSettlement(userPrincipal.getId(), request);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
            summary = "사용자의 정착 리포트 목록 조회",
            description = "현재 로그인한 사용자의 모든 정착 리포트 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "정착 리포트 목록 조회 성공")
    public ResponseEntity<List<SettlementRecommendationResponse>> getUserReports(
            @CurrentUser UserPrincipal userPrincipal
            ){
        List<SettlementRecommendationResponse> reports = settlementService.getUserReports(userPrincipal.getId());
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{reportId}")
    @Operation(
            summary = "특정 정착 리포트 상세 조회",
            description = "특정 ID의 정착 리포트 상세 정보를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "정착 리포트 상세 조회 성공")
    public ResponseEntity<SettlementRecommendationResponse> getReportById(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long reportId
    ) {
        SettlementRecommendationResponse report = settlementService.getReportById(userPrincipal.getId(), reportId);
        return ResponseEntity.ok(report);
    }

}
