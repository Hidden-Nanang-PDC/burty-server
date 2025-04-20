package org.example.burtyserver.domain.settlement.model.repository;

import org.example.burtyserver.domain.settlement.model.entity.SettlementReport;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 정착 리포트 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface SettlementReportRepository extends JpaRepository<SettlementReport, Long> {

    /**
     * 사용자별 정착 리포트 목록 조회
     * @param user 사용자
     * @return 정착 리포트 목록
     */
    List<SettlementReport> findByUserOrderByCreatedAtDesc(User user);

    Optional<SettlementReport> findByUserAndId(User user, Long id);
}
