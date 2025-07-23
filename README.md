# SangSangPlus Product Service

MSA 기반 상품 관리 서비스입니다. Product와 ProductImage 정보를 CRUD하며, Kafka를 통한 이벤트 기반 아키텍처와 CQRS, Saga 패턴을 구현합니다.

## 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **Message Broker**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **Container**: Docker
- **Orchestration**: Kubernetes (Azure AKS)

## 주요 기능

### 1. Product CRUD
- 상품 생성, 조회, 수정, 삭제
- 사용자별 상품 관리
- 카테고리별 상품 조회
- 키워드 검색

### 2. ProductImage 관리
- 상품 이미지 추가/삭제
- 이미지 순서 관리
- 대체 텍스트 지원

### 3. 이벤트 기반 아키텍처
- Kafka를 통한 이벤트 발행/구독
- 이벤트 종류:
  - PRODUCT_CREATED
  - PRODUCT_UPDATED
  - PRODUCT_DELETED
  - PRODUCT_IMAGE_ADDED
  - PRODUCT_IMAGE_REMOVED

### 4. CQRS 패턴
- Command와 Query 서비스 분리
- 읽기 최적화를 위한 캐싱

### 5. Saga 패턴
- 분산 트랜잭션 관리
- 보상 트랜잭션 구현
- 사용자 삭제 시 연관 상품 처리

## API 엔드포인트

### Query Endpoints
- `GET /api/products/{productId}` - 상품 상세 조회
- `GET /api/products` - 전체 상품 목록
- `GET /api/products/user/{userId}` - 사용자별 상품 목록
- `GET /api/products/category/{category}` - 카테고리별 상품 목록
- `GET /api/products/search?keyword={keyword}` - 상품 검색
- `GET /api/products/categories` - 전체 카테고리 목록

### Command Endpoints
- `POST /api/products` - 상품 생성
- `PUT /api/products/{productId}` - 상품 수정
- `DELETE /api/products/{productId}` - 상품 삭제
- `POST /api/products/{productId}/images` - 이미지 추가
- `DELETE /api/products/{productId}/images/{imageId}` - 이미지 삭제

## 실행 방법

### 로컬 개발 환경

1. **필수 요구사항**
   - JDK 17
   - Docker & Docker Compose
   - Maven

2. **인프라 실행**
   ```bash
   docker-compose up -d postgres zookeeper kafka
   ```

3. **애플리케이션 실행**
   ```bash
   ./mvnw spring-boot:run
   ```

### Docker 환경

```bash
# 이미지 빌드
docker build -t buildingbite/sangsangplus-product:latest .

# 전체 서비스 실행
docker-compose up -d
```

### Kubernetes 배포

```bash
# 네임스페이스 생성
kubectl apply -f k8s/namespace.yaml

# 설정 및 시크릿 생성
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 서비스 배포
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml
kubectl apply -f k8s/ingress.yaml
```

## 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| DB_HOST | PostgreSQL 호스트 | localhost |
| DB_PORT | PostgreSQL 포트 | 5432 |
| DB_NAME | 데이터베이스 이름 | productdb |
| DB_USERNAME | DB 사용자명 | postgres |
| DB_PASSWORD | DB 비밀번호 | postgres |
| KAFKA_BOOTSTRAP_SERVERS | Kafka 서버 주소 | localhost:9092 |
| EUREKA_SERVER_URL | Eureka 서버 URL | http://localhost:8761/eureka/ |
| JWT_ISSUER_URI | JWT 발급자 URI | http://localhost:8080 |

## 프로젝트 구조

```
product-service/
├── src/main/java/com/sangsangplus/productservice/
│   ├── domain/
│   │   └── entity/         # JPA 엔티티
│   ├── dto/
│   │   ├── request/        # 요청 DTO
│   │   └── response/       # 응답 DTO
│   ├── repository/         # JPA Repository
│   ├── service/
│   │   ├── command/        # Command 서비스 (CUD)
│   │   └── query/          # Query 서비스 (R)
│   ├── controller/         # REST 컨트롤러
│   ├── event/
│   │   ├── publisher/      # 이벤트 발행
│   │   └── listener/       # 이벤트 구독
│   ├── saga/              # Saga 패턴 구현
│   ├── config/            # 설정 클래스
│   ├── security/          # 보안 설정
│   ├── mapper/            # MapStruct 매퍼
│   └── exception/         # 예외 처리
├── src/main/resources/
│   └── application.yml    # 애플리케이션 설정
├── k8s/                   # Kubernetes 매니페스트
├── docker-compose.yml     # Docker Compose 설정
├── Dockerfile            # Docker 이미지 빌드
└── pom.xml              # Maven 설정
```

## 모니터링

- Health Check: `http://localhost:8082/actuator/health`
- Metrics: `http://localhost:8082/actuator/metrics`
- Prometheus: `http://localhost:8082/actuator/prometheus`

## 개발 가이드

1. **브랜치 전략**
   - main: 프로덕션 브랜치
   - develop: 개발 브랜치
   - feature/*: 기능 개발 브랜치

2. **커밋 메시지 규칙**
   - feat: 새로운 기능
   - fix: 버그 수정
   - docs: 문서 수정
   - refactor: 코드 리팩토링
   - test: 테스트 코드

3. **코드 스타일**
   - Google Java Style Guide 준수
   - Lombok 사용으로 보일러플레이트 최소화