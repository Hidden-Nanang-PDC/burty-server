package org.example.burtyserver.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.user.entity.User;
import org.example.burtyserver.domain.user.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security에서 사용자 정보를 로드하는 서비스
 * 사용자명(이메일)이나 ID로 사용자 정보를 조회하여 UserDetails 객체로 변환
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * 사용자명(이메일)으로 사용자 정보 로드
     * UserDetailsService 인터페이스 구현 메서드
     *
     * @param email 사용자 이메일
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        return UserPrincipal.create(user);
    }

    /**
     * 사용자 ID로 사용자 정보 로드
     * JWT 토큰에서 추출한 ID로 사용자 정보를 조회할 때 사용
     *
     * @param id 사용자 ID
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));

        return UserPrincipal.create(user);
    }
}
