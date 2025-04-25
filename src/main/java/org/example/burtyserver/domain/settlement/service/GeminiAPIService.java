package org.example.burtyserver.domain.settlement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Google Gemini API와 통신하는 서비스
 */
@Service
@RequiredArgsConstructor
public class GeminiAPIService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value(("${app.gemini.model}"))
    private String model;

    /**
     * Gemini API를 호출해 정착지 추천을 받아옵니다.
     *
     * @param age 사용자 나이
     * @param desiredJob 희망 직무
     * @param monthlyFixedCost 월 고정비
     * @return API 응답 JSON형식의 문자열
     */
    public String getSettlementRecommendation(Integer age, String desiredJob, Integer monthlyFixedCost){
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        String prompt = buildPrompt(age, desiredJob, monthlyFixedCost);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", prompt);
        content.put("parts", List.of(part));
        requestBody.put("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(url, request, String.class);
    }

    /**
     * Gemini API 프롬프트를 구성합니다.
     *
     * @param age 사용자 나이
     * @param desiredJob 희망 직무
     * @param monthlyFixedCost 월 고정비
     * @return 구성된 프롬프트 string
     */
    private String buildPrompt(Integer age, String desiredJob, Integer monthlyFixedCost){
        return String.format(
                "당신은 청년들에게 최적의 정착지를 추천해주는 전문가입니다. " +
                        "다음 정보를 바탕으로 대한민국 내에서 가장 적합한 정착지 1곳을 추천해주세요.\n\n" +
                        "* 나이: %d세\n" +
                        "* 희망 직무: %s\n" +
                        "* 월 고정비: %d원\n\n" +
                        "다음 JSON 형식으로 답변해주세요:\n" +
                        "{\n" +
                        "  \"recommendedArea\": \"추천 지역명(시/군/구까지 구체적으로)\",\n" +
                        "  \"shortRecommendationReason\": \"추천 사유를 한 문장으로 간결하게 요약\",\n" +
                        "  \"recommendationReason\": \"추천 사유(직무 연관성, 생활비, 정책 등 훨씬 더 구체적인 이유)\",\n" +
                        "  \"savingPotential\": \"해당 지역 예상 저축 가능성(직무별 평균 소득 - 고정비 - 지역 평균 월세)형식으로.\",\n" +
                        "  \"averageRent\": 추천 지역의 평균 월세(숫자만, 단위 없이),\n" +
                        "  \"policies\": [\n" +
                        "    {\"name\": \"정책명1\", \"description\": \"정책 설명1\", \"url\": \"정책 URL1(선택)\"},\n" +
                        "    {\"name\": \"정책명2\", \"description\": \"정책 설명2\", \"url\": \"정책 URL2(선택)\"}\n" +
                        "  ],\n" +
                        "}\n\n" +
                        "데이터에 기반한 실질적인 도움이 되는 추천을 해주시고, shortRecommendationReason은 반드시 한 문장으로 간결하게 요약해주세요. averageRent는 평균 월세 금액을 숫자로만 제공해주세요(예: 450000).",
                age, desiredJob, monthlyFixedCost
        );
    }
}
