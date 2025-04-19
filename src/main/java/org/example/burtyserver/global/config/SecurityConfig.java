package org.example.burtyserver.global.config;

import lombok.RequiredArgsConstructor;
import org.example.burtyserver.global.security.jwt.JwtAuthenticationFilter;
import org.example.burtyserver.global.security.oauth2.CustomUserDetailsService;
import org.example.burtyserver.global.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.example.burtyserver.global.security.oauth2.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * 인증, 인가, 필터 등 보안 관련 설정을 정의
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 필요한 서비스 및 필터 주입
    private final OAuth2UserService oAuth2UserService;                             // OAuth2 사용자 정보 처리 서비스
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;  // OAuth2 인증 성공 핸들러
    private final CustomUserDetailsService customUserDetailsService;               // 사용자 상세 정보 서비스
    private final JwtAuthenticationFilter jwtAuthenticationFilter;                 // JWT 인증 필터

    /**
     * Spring Security 필터 체인 설정
     * 인증, 인가, OAuth2, JWT 필터 등을 구성
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능 비활성화 (배포 시 다시 점검)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 비활성화 (배포 시 다시 점검)
                .cors(AbstractHttpConfigurer::disable)
                // JWT 사용으로 세션 불필요 (배포 시 다시 점검)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                // 요청별 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Swagger 관련 접근 허용
                        .requestMatchers("/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/webjars/**").permitAll()
                        .anyRequest().authenticated())
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 인증 엔드포인트 설정
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorize"))
                        // 리다이렉션 엔드포인트 설정
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/*"))
                        // 사용자 정보 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                        // 인증 성공 처리 핸들러 설정
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );

        // JWT 인증 필터 추가 - UsernamePasswordAuthenticationFilter 앞에 배치
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 인코더 Bean 등록
     * 비밀번호 해싱에 BCrypt 알고리즘 사용
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 Bean 등록
     * Spring Security의 인증 처리를 위한 AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}