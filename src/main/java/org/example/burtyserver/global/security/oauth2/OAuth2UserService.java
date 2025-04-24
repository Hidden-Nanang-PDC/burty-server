package org.example.burtyserver.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.burtyserver.domain.user.model.entity.User;
import org.example.burtyserver.domain.user.model.entity.UserAuthority;
import org.example.burtyserver.domain.user.model.repository.UserAuthorityRepository;
import org.example.burtyserver.domain.user.model.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.example.burtyserver.global.security.dto.OAuth2UserInfo;
import org.example.burtyserver.global.security.dto.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("OAuth2 사용자 정보 요청 시작: {}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oAuth2User = super.loadUser(userRequest);

        try{
            log.debug("OAuth2 사용자 속성: {}", oAuth2User.getAttributes());
            return processOAuth2User(userRequest, oAuth2User);
        } catch(Exception ex) {
            log.error("OAuth2 사용자 처리 중 오류: ", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        log.debug("소셜 로그인 제공자: {}", registrationId);
        User.AuthProvider provider = getProvider(registrationId);

        // attributes가 null이 아닌지 확인
        if (oAuth2User.getAttributes() == null) {
            log.error("OAuth2 사용자 속성이 null입니다.");
            throw new OAuth2AuthenticationException("OAuth2 사용자 속성이 존재하지 않습니다.");
        }

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        // 이메일 정보 확인 및 생성
        String email = oAuth2UserInfo.getEmail();
        if (email == null || email.isEmpty()) {
            email = provider.toString().toLowerCase() + "_" + oAuth2UserInfo.getId() + "@kakao.user.com";
            log.debug("이메일 정보가 없어 생성된 이메일: {}", email);
        }

        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, oAuth2UserInfo.getId());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();

            // 추가: 탈퇴한 사용자(active=false) 확인 및 처리
            if (!user.isActive()) {
                log.warn("탈퇴한 사용자가 로그인을 시도했습니다: {}", user.getId());
                throw new OAuth2AuthenticationException("탈퇴한 계정입니다. 관리자에게 문의하세요.");
            }

            // 사용자 정보 업데이트
            user = user.update(
                    oAuth2UserInfo.getName(),
                    oAuth2UserInfo.getImageUrl()
            );
            user = userRepository.save(user);
        } else {
            // 새 사용자 생성
            user = createUser(oAuth2UserInfo, provider, email);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo, User.AuthProvider provider, String email) {

        User user = User.builder()
                .email(email)
                .name(oAuth2UserInfo.getName())
                .profileImageUrl(oAuth2UserInfo.getImageUrl())
                .provider(provider)
                .providerId(oAuth2UserInfo.getId())
                .role(User.Role.ROLE_USER)
                // 닉네임, 지역, 나이는 초기값 null로 설정
                .nickname(null)
                .region(null)
                .birthDate(null)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        // 기본 권한 추가
        UserAuthority authority = UserAuthority.builder()
                .authority("ROLE_USER")
                .user(savedUser)
                .build();
        userAuthorityRepository.save(authority);

        return savedUser;
    }

    private User.AuthProvider getProvider(String registrationId){
        if ("kakao".equals(registrationId)) {
            return User.AuthProvider.KAKAO;
        } else if ("google".equals(registrationId)) {
            return User.AuthProvider.GOOGLE;
        } else if ("naver".equals(registrationId)) {
            return User.AuthProvider.NAVER;
        }
        return User.AuthProvider.LOCAL;
    }
}
