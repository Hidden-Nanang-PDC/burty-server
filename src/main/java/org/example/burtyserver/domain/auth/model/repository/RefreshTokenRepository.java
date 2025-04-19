package org.example.burtyserver.domain.auth.model.repository;

import org.example.burtyserver.domain.auth.model.entity.RefreshToken;
import org.example.burtyserver.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserAndRevokedFalse(User user);
    boolean existByUserAndRevokedFalse(User user);
}
