package org.example.burtyserver.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 권한 정보를 저장하는 엔티티 클래스
 * User 엔티티와 다대일(N:1) 관계
 */
@Entity
@Table(name = "user_authorities")  // 테이블명 지정
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    // User 엔티티와의 다대일 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)  // 지연 로딩 사용
    @JoinColumn(name = "user_id")       // 외래키 컬럼명
    private User user;                   // 사용자 참조

    // 권한 문자열 (예: "ROLE_USER", "ROLE_ADMIN")
    @Column(nullable = false)
    private String authority;
}