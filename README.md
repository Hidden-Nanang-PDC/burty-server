# Burty Server

- Spring Security와 Spring OAuth2를 활용하여 카카오, 구글, 네이버 소셜 로그인을 구현
- Google Gemini API를 활용한 청년 정착지 추천 기능 제공

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
- Google Gemini API (AI 생성형 모델)
- iText (PDF 생성)

## 프로젝트 구조

```
org.example.burtyserver/
├── domain/                  # 도메인 중심 패키지
│   ├── auth/                # 인증 관련 기능
│   │   ├── controller/      # API 컨트롤러
│   │   └── dto/             # 데이터 전송 객체
│   │
│   ├── user/                # 사용자 관련 기능
│   │   ├── entity/          # 엔티티 클래스
│   │   ├── repository/      # 데이터 접근 계층
│   │   └── service/         # 비즈니스 로직
│   │
│   └── settlement/          # 정착지 추천 관련 기능
│       ├── controller/      # API 컨트롤러
│       ├── model/           # 모델 클래스
│       │   ├── dto/         # 데이터 전송 객체
│       │   ├── entity/      # 엔티티 클래스
│       │   └── repository/  # 데이터 접근 계층
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
- `/api/auth/logout`: 로그아웃 (POST)

### OAuth2 관련 엔드포인트

- `/oauth2/authorize/{provider}`: 소셜 로그인 요청 (GET)
  - 예: `/oauth2/authorize/kakao`: 카카오 로그인 요청
- `/login/oauth2/code/{provider}`: 소셜 로그인 콜백 엔드포인트

### 사용자 프로필 관련 API

- `/api/user/profile`: 사용자 프로필 정보 업데이트 (PUT)

### 정착지 추천 관련 API

- `/api/settlements/recommend`: 청년 정착지 추천 요청 (POST)
- `/api/settlements`: 사용자의 정착 리포트 목록 조회 (GET)
- `/api/settlements/{reportId}`: 특정 정착 리포트 상세 조회 (GET)
- `/api/settlements/{reportId}/pdf`: 정착 리포트 PDF 다운로드 (GET)

## 청년 정착지 추천 기능

Google Gemini API를 활용하여 청년들에게 최적의 정착지를 추천해주는 기능을 제공합니다.

### 기술 구현
- Google Gemini API를 활용한 AI 기반 정착지 추천
- Spring RestTemplate을 활용한 외부 API 통신
- Jackson 라이브러리를 활용한 JSON 파싱
- JPA를 활용한 데이터 저장 및 관리
- iText 라이브러리를 활용한 PDF 생성
