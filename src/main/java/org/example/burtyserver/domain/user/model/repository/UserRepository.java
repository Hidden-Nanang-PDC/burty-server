package org.example.burtyserver.domain.user.model.repository;

import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티에 대한 데이터 접근 인터페이스
 * Spring Data JPA를 사용하여 기본적인 CRUD 기능 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 찾기
     * @param email 사용자 이메일
     * @return 사용자 정보
     */
    Optional<User> findByEmail(String email);

    /**
     * 소셜 로그인 제공자와 제공자 ID로 사용자 찾기
     * @param provider 인증 제공자 (KAKAO, GOOGLE, NAVER)
     * @param providerId 제공자에서의 사용자 ID
     * @return 사용자 정보
     */
    Optional<User> findByProviderAndProviderId(User.AuthProvider provider, String providerId);

    /**
     * 이메일 존재 여부 확인
     * @param email 사용자 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}