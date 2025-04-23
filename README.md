# 버티 Server

SpringBoot 기반의 청년 정착지 추천 및 커뮤니티 백엔드 서비스

## 📋 소개

버티 Server는 다음 기능을 제공하는 백엔드 애플리케이션입니다:
- **소셜 로그인**: Spring Security와 OAuth2를 활용한 카카오, 구글 소셜 로그인
- **청년 정착지 추천**: Google Gemini API를 활용해 사용자의 나이, 직무, 예산에 맞는 최적의 정착지 추천
- **커뮤니티 시스템**: 지역/관심사 기반 게시글 작성, 댓글, 좋아요 등 소통 기능

## 🔗 API 문서

[Burty API 문서](https://burty-server.onrender.com/swagger-ui/index.html)

## 📊 데이터베이스 설계

[ERD 모델](https://www.erdcloud.com/d/WhRKNkXzxhCqTsNgD)

## 🛠️ 기술 스택

### 백엔드
- **Java 17**
- **Spring Boot 3.4.4**
- **Spring Security & JWT**: 인증/인가 처리
- **Spring OAuth2 Client**: 소셜 로그인 구현
- **Spring Data JPA**: 데이터 액세스 계층
- **MySQL**: 데이터베이스

### DevOps
- **Docker**: 컨테이너화 및 배포
- **GitHub Actions**: CI/CD 파이프라인

### 외부 API
- **Google Gemini API**: AI 생성형 모델을 활용한 정착지 추천

## 🏗️ 프로젝트 구조

```
org.example.burtyserver/
├── domain/                  # 도메인 중심 패키지
│   ├── auth/                # 인증 관련 기능
│   │   ├── controller/      # API 컨트롤러
│   │   ├── model/           # 엔티티 및 레포지토리
│   │   ├── dto/             # 데이터 전송 객체
│   │   └── service/         # 비즈니스 로직
│   │
│   ├── user/                # 사용자 관련 기능
│   │   ├── controller/      # API 컨트롤러
│   │   ├── model/           # 엔티티 및 레포지토리
│   │   └── service/         # 비즈니스 로직
│   │
│   ├── community/           # 커뮤니티 관련 기능
│   │   ├── controller/      # API 컨트롤러
│   │   ├── model/           # 엔티티 및 레포지토리
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

## 🔐 인증 및 보안

- **JWT 기반 인증**: Access Token과 Refresh Token을 활용한 인증 시스템
- **OAuth2 소셜 로그인**: 카카오, 구글 로그인 지원
- **Spring Security**: 엔드포인트 보안 및 인가 처리

## 🏙️ 정착지 추천 기능

Google Gemini API를 활용하여 청년들에게 최적의 정착지를 추천해주는 기능을 제공합니다.

### 주요 기능
- 사용자의 나이, 희망 직무, 월 고정비를 고려한 추천
- 추천된 지역의 상세 정보 및 추천 이유 제공
- 해당 지역 관련 청년 지원 정책 정보 제공
- 예상 저축 가능성 분석

## 🧑‍🤝‍🧑 커뮤니티 기능

지역 기반 커뮤니티 시스템으로 정착 관련 정보 공유 및 소통을 지원합니다.

### 주요 기능
- **게시글 관리**: 작성, 수정, 삭제, 조회
- **댓글 시스템**: 게시글에 댓글 작성, 수정, 삭제
- **좋아요 기능**: 게시글 및 댓글에 좋아요 추가/취소
- **카테고리 시스템**: 주제별 게시글 분류
- **키워드 자동 분류**: 게시글 내용 분석을 통한 자동 카테고리 매핑
- **마이페이지 기능**: 내가 작성한 글, 댓글, 좋아요한 글 모아보기

## 📑 주요 API 엔드포인트

### 인증 관련 API
- `GET /api/auth/me`: 현재 로그인한 사용자 정보 조회
- `POST /api/auth/logout`: 로그아웃

### OAuth2 관련 엔드포인트
- `GET /oauth2/authorize/{provider}`: 소셜 로그인 요청
- `GET /login/oauth2/code/{provider}`: 소셜 로그인 콜백 엔드포인트

### 사용자 프로필 관련 API
- `PUT /api/user/profile`: 사용자 프로필 정보 업데이트

### 정착지 추천 관련 API
- `POST /api/settlements/recommend`: 청년 정착지 추천 요청
- `GET /api/settlements`: 사용자의 정착 리포트 목록 조회
- `GET /api/settlements/{reportId}`: 특정 정착 리포트 상세 조회
- `GET /api/settlements/{reportId}/pdf`: 정착 리포트 PDF 다운로드

### 커뮤니티 관련 API
- `GET/POST /api/community/posts`: 게시글 목록 조회 및 작성
- `GET/PUT/DELETE /api/community/posts/{postId}`: 게시글 상세/수정/삭제
- `GET /api/community/posts/category/{categoryId}`: 카테고리별 게시글 조회
- `GET /api/community/posts/my`: 내가 작성한 게시글 조회
- `POST/DELETE /api/community/posts/{postId}/likes`: 게시글 좋아요/취소
- `GET /api/community/posts/{postId}/likes/status`: 좋아요 상태 확인
- `GET /api/community/posts/{postId}/comments`: 게시글 댓글 목록 조회
- `POST /api/community/posts/{postId}/comments`: 댓글 작성
- `PUT/DELETE /api/community/posts/{postId}/comments/{commentId}`: 댓글 수정/삭제

## 🚀 배포 및 CI/CD

GitHub Actions를 활용한 CI/CD 파이프라인:
- 메인 브랜치 푸시 시 자동 빌드
- Docker 이미지 생성 및 GitHub Container Registry 업로드
- Docker Layer 캐싱을 통한 빌드 최적화

## 📝 구현 특징 및 기술적 고려사항

### 도메인 주도 설계 (DDD)
- 도메인별 패키지 구조로 관심사 분리
- 각 도메인의 독립적인 비즈니스 로직 캡슐화

### RESTful API 설계
- 자원 중심의 URL 구조
- HTTP 메소드를 활용한 CRUD 연산 표현
- 상태 코드를 통한 명확한 응답 전달

### 보안 설계
- JWT 토큰 기반 인증과 인가
- Refresh Token 전략으로 보안성 강화
- Spring Security를 활용한 메소드 수준 보안

### 데이터 접근 최적화
- Fetch Type 조정을 통한 N+1 문제 방지
- 데이터 조회 시 필요한 연관 관계만 로딩

### 확장성 고려
- 멀티 소셜 로그인 제공자 쉽게 추가 가능
- 새로운 도메인 추가가 용이한 패키지 구조