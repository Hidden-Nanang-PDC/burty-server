package org.example.burtyserver.domain.settlement.model.entity;

import io.swagger.v3.oas.annotations.media.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.example.burtyserver.domain.user.model.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자의 정착 추천 요청 및 결과를 저장하는 엔티티
 * 사용자의 입력값과 AI 추천 결과를 포함합니다.
 */
@Entity
@Table(name = "settlement_reports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer age;
    private String desiredJob;
    private Integer monthlyFixedCost;

    private String recommendedArea;
    @Column(length = 2000)
    private String recommendationReason;
    @Column(length = 1000)
    private String savingPotential;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // AI 응답의 정책 리스트와 1:N 관계
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettlementPolicy> policies = new ArrayList<>();

    /**
     * AI 응답의 정책 추가 메서드
     * @param policy 추가할 정책
     */
    public void addPolicy(SettlementPolicy policy) {
        policies.add(policy);
        policy.setReport(this);
    }

}
