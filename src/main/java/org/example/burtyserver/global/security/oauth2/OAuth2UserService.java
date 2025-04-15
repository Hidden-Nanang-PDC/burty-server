package org.example.burtyserver.global.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.example.burtyserver.domain.user.entity.User;
import org.example.burtyserver.domain.user.entity.UserAuthority;
import org.example.burtyserver.domain.user.repository.UserAuthorityRepository;
import org.example.burtyserver.domain.user.repository.UserRepository;
import org.example.burtyserver.global.security.UserPrincipal;
import org.example.burtyserver.global.security.dto.OAuth2UserInfo;
import org.example.burtyserver.global.security.dto.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try{
            return processOAuth2User(userRequest, oAuth2User);
        } catch(Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User){
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        User.AuthProvider provider = getProvider(registrationId);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByProviderAndProviderId(provider, oAuth2UserInfo.getId());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 사용자 정보 업데이트
            user = user.update(
                    oAuth2UserInfo.getName(),
                    oAuth2UserInfo.getImageUrl()
            );
            user = userRepository.save(user);
        } else {
            // 새 사용자 생성
            user = createUser(oAuth2UserInfo, provider);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo, User.AuthProvider provider) {
        User user = User.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .profileImageUrl(oAuth2UserInfo.getImageUrl())
                .provider(provider)
                .providerId(oAuth2UserInfo.getId())
                .role(User.Role.ROLE_USER)
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
