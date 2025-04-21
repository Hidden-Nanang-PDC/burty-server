package org.example.burtyserver.domain.settlement.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

@Entity
@Table(name = "settlement_policies")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyName;

    @Column(length=1000)
    private String policyDescription;

    private String policyUrl;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private SettlementReport report;

}
