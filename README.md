# Burty Server

Spring Security와 Spring OAuth2를 활용하여 카카오, 구글, 네이버 소셜 로그인을 구현
+ 진행중..

## DataBase
[ERD 모델](https://www.erdcloud.com/d/WhRKNkXzxhCqTsNgD)

## 기술 스택

- Java 17
- Spring Boot 3.4.4
- Spring Security
- Spring OAuth2 Client
- Spring Data JPA
- MySQL
- JWT (JSON Web Token)
- Lombok

## 프로젝트 구조

```
org.example.burtyserver/
├── domain/                  # 도메인 중심 패키지
│   ├── auth/                # 인증 관련 기능
│   │   ├── controller/      # API 컨트롤러
│   │   └── dto/             # 데이터 전송 객체
│   │
│   └── user/                # 사용자 관련 기능
│       ├── entity/          # 엔티티 클래스
│       ├── repository/      # 데이터 접근 계층
│       └── service/         # 비즈니스 로직
│
├── global/                  # 공통/전역 기능
│   ├── config/              # 설정 클래스
│   ├── exception/           # 예외 처리
│   └── security/            # 보안 관련 클래스
│       ├── jwt/             # JWT 관련 클래스
│       ├── oauth2/          # OAuth2 관련 클래스
│       └── dto/             # 보안 관련 DTO
│
└── BurtyServerApplication.java   # 애플리케이션 진입점
```

## API 엔드포인트

### 인증 관련 API

- `/api/auth/me`: 현재 로그인한 사용자 정보 조회 (GET)

### OAuth2 관련 엔드포인트

- `/oauth2/authorize/{provider}`: 소셜 로그인 요청 (GET)
    - 예: `/oauth2/authorize/kakao`: 카카오 로그인 요청
- `/login/oauth2/code/{provider}`: 소셜 로그인 콜백 엔드포인트

