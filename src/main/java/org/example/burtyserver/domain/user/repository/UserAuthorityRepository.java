package org.example.burtyserver.domain.user.repository;

import org.example.burtyserver.domain.user.entity.User;
import org.example.burtyserver.domain.user.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserAuthority 엔티티에 대한 데이터 접근 인터페이스
 * 사용자 권한 정보를 관리
 */
@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    /**
     * 특정 사용자의 모든 권한 조회
     * @param user 사용자 객체
     * @return 사용자가 가진 권한 목록
     */
    List<UserAuthority> findAllByUser(User user);
}