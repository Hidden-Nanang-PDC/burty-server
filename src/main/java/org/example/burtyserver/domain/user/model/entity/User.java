package org.example.burtyserver.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 사용자 정보를 저장하는 엔티티 클래스
 * 소셜 로그인 정보와 사용자 기본 정보를 포함
 */
@Entity
@Table(name = "users")  // 테이블명 지정
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private String name;
    private String profileImageUrl;  // 프로필 이미지 URL

    @Column(length = 50)
    private String nickname;  // 사용자 닉네임

    @Column(length = 100)
    private String region;    // 사용자 지역

    @Column
    private LocalDate birthDate;      // 사용자 생년월일

    @Column(length = 50)
    private String job;  // 사용자 닉네임

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private Role role;  // 사용자 역할 (ROLE_USER, ROLE_ADMIN)

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;  // 인증 제공자 (KAKAO, GOOGLE, NAVER, LOCAL)
    private String providerId;  // 인증 제공자에서의 사용자 ID

    @CreationTimestamp
    private LocalDateTime createdAt;  // 생성 일시

    @UpdateTimestamp
    private LocalDateTime updatedAt;  // 수정 일시

    // 사용자 권한 정보 (1:N 관계)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserAuthority> authorities = new HashSet<>();

    /**
     * 사용자 역할 정의
     */
    public enum Role {
        ROLE_USER,   // 일반 사용자
        ROLE_ADMIN   // 관리자
    }

    /**
     * 인증 제공자 정의
     */
    public enum AuthProvider {
        LOCAL,   // 일반 로그인
        KAKAO,   // 카카오 로그인
        GOOGLE,  // 구글 로그인
        NAVER    // 네이버 로그인
    }

    /**
     * 사용자 정보 업데이트
     * 소셜 로그인 시 사용자 정보가 변경되었을 때 호출
     */
    public User update(String name, String profileImageUrl) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        return this;
    }

    /**
     * 사용자 추가 프로필 정보 업데이트
     */
    public User updateProfile(String nickname, String region, LocalDate birthDate, String job) {
        this.nickname = nickname;
        this.region = region;
        this.birthDate = birthDate;
        this.job = job;
        return this;
    }

    /**
     * 사용자가 특정 권한을 가지고 있는지 확인
     */
    public boolean hasAuthority(String authority) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    public User setActive(boolean active) {
        this.active = active;
        return this;
    }

    public User deactivateAccount() {
        this.active = false;
        this.email = "deleted_" +this.id + "@example.com";
        this.name = "탈퇴한 사용자";
        this.profileImageUrl = null;
        this.nickname = null;
        this.region = null;
        this.birthDate = null;
        this.job = null;
        return this;
    }
}