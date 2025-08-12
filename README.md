# SangSangPlus Product Service

중고 거래 플랫폼 SangSangPlus의 상품 관리 마이크로서비스입니다.

## 📋 개요

- **서비스명**: Product Service
- **포트**: 8082
- **데이터베이스**: Azure PostgreSQL
- **인증**: X-User-Id 헤더 기반 (게이트웨이에서 JWT 파싱 후 전달)
- **이벤트**: Azure Event Hubs
- **사용자 ID**: UUID 형식

## 🚀 주요 기능

- 상품 CRUD 작업
- 상품 이미지 관리
- 카테고리별 상품 조회
- 사용자별 상품 관리
- 상품 검색 기능
- 실시간 이벤트 발행 (Azure Event Hubs)

## 📡 API 엔드포인트

### 공개 엔드포인트 (인증 불필요)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/health` | 서비스 헬스체크 |
| GET | `/api/products` | 전체 상품 조회 (페이징) |
| GET | `/api/products/{productId}` | 특정 상품 상세 조회 |
| GET | `/api/products/categories` | 전체 카테고리 목록 |
| GET | `/api/products/recent?limit={limit}` | 최근 상품 조회 |
| GET | `/api/products/search?keyword={keyword}` | 상품 검색 |
| GET | `/api/products/user/{userId}` | 특정 사용자 상품 조회 |
| GET | `/api/products/category/{category}` | 카테고리별 상품 조회 |
| GET | `/api/products/{productId}/details` | 상품 상세 정보 조회 |

### 인증 필요 엔드포인트 (X-User-Id 헤더 필요)

| Method | Endpoint | Description | 권한 |
|--------|----------|-------------|------|
| POST | `/api/products` | 상품 등록 | 인증된 사용자 |
| PUT | `/api/products/{productId}` | 상품 수정 | 상품 소유자 |
| DELETE | `/api/products/{productId}` | 상품 삭제 | 상품 소유자 |
| POST | `/api/products/{productId}/images` | 상품 이미지 추가 | 상품 소유자 |
| DELETE | `/api/products/{productId}/images/{imageId}` | 상품 이미지 삭제 | 상품 소유자 |
| PUT | `/api/products/{productId}/details` | 상품 상세 정보 생성/수정 | 상품 소유자 |
| DELETE | `/api/products/{productId}/details` | 상품 상세 정보 삭제 | 상품 소유자 |
| GET | `/api/products/my` | 내 상품 조회 | 인증된 사용자 |
| GET | `/api/products/my/category/{category}` | 내 상품 카테고리별 조회 | 인증된 사용자 |

### 관리자 엔드포인트 (X-User-Role: ADMIN 필요)

| Method | Endpoint | Description | 권한 |
|--------|----------|-------------|------|
| PUT | `/api/products/admin/{productId}` | 관리자 상품 수정 | ADMIN |
| DELETE | `/api/products/admin/{productId}` | 관리자 상품 삭제 | ADMIN |
| PUT | `/api/products/admin/{productId}/details` | 관리자 상품 상세 정보 생성/수정 | ADMIN |
| DELETE | `/api/products/admin/{productId}/details` | 관리자 상품 상세 정보 삭제 | ADMIN |

## 🔐 인증 방식

게이트웨이에서 JWT를 파싱하여 다음 헤더로 전달:

- **X-User-Id**: 사용자 UUID (예: `550e8400-e29b-41d4-a716-446655440001`)
- **X-User-Role**: 사용자 역할 (`USER` 또는 `ADMIN`)

```bash
# API 호출 예시
curl -X POST "http://localhost:8082/api/products" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440001" \
  -H "X-User-Role: USER" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "중고 노트북",
    "description": "MacBook Pro 2022",
    "category": "전자제품",
    "price": 1500000
  }'
```

## 🗄️ 데이터베이스 스키마

### Products 테이블

```sql
CREATE TABLE products (
    product_id BIGSERIAL PRIMARY KEY,
    user_id UUID NOT NULL,                    -- 상품 소유자 UUID
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Product Images 테이블

```sql
CREATE TABLE product_images (
    image_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    url VARCHAR(500) NOT NULL,
    alt_text VARCHAR(255),
    display_order INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE
);
```

### Product Details 테이블

```sql
CREATE TABLE product_details (
    product_id BIGINT PRIMARY KEY,
    content TEXT NOT NULL,                    -- HTML 상세 내용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) 
        REFERENCES products(product_id) 
        ON DELETE CASCADE
);
```

## 🔧 환경 변수

### 필수 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| `DB_HOST` | PostgreSQL 서버 호스트 | `sangsangplus-postgre-server.postgres.database.azure.com` |
| `DB_PORT` | PostgreSQL 포트 | `5432` |
| `DB_NAME` | 데이터베이스 이름 | `product_db` |
| `DB_USERNAME` | 데이터베이스 사용자명 | `sangsangplus_admin` |
| `DB_PASSWORD` | 데이터베이스 비밀번호 | - |

### 선택적 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| `PRODUCT_EVENTHUB_CONNECTION_STRING` | 상품 이벤트 발행용 Event Hubs 연결 문자열 | - |
| `USER_EVENTHUB_CONNECTION_STRING` | 유저 이벤트 수신용 Event Hubs 연결 문자열 | - |

## 🏗️ 개발 환경 구축

### 1. 사전 요구사항

```bash
# setup.sh 스크립트로 개발 환경 자동 구성
chmod +x setup.sh
./setup.sh
```

설치되는 도구들:
- Java 17+
- Maven 3.6+
- Docker
- kubectl
- Azure CLI

### 2. 프로젝트 클론 및 빌드

```bash
git clone https://github.com/sunflower-class/sangsang-plus-product.git
cd sangsang-plus-product

# 프로젝트 빌드
mvn clean package
```

### 3. 데이터베이스 마이그레이션

UUID 형식 사용자 ID로 변경하려면 마이그레이션 실행:

```bash
# PostgreSQL 연결 후 마이그레이션 실행
psql -h sangsangplus-postgre-server.postgres.database.azure.com \
     -U sangsangplus_admin \
     -d product_db \
     -f scripts/migrate-to-uuid.sql
```

⚠️ **주의**: 이 마이그레이션은 기존 데이터를 모두 삭제합니다!

### 4. 로컬 실행

```bash
# PostgreSQL 연결 정보 설정
export DB_HOST=sangsangplus-postgre-server.postgres.database.azure.com
export DB_USERNAME=sangsangplus_admin
export DB_PASSWORD=your_password

# Event Hubs 연결 정보 설정 (선택사항)
export PRODUCT_EVENTHUB_CONNECTION_STRING="Endpoint=sb://sangsangplus-eventhubs.servicebus.windows.net/;SharedAccessKeyName=ProductProducerKey;SharedAccessKey=your_key;EntityPath=product-events"
export USER_EVENTHUB_CONNECTION_STRING="Endpoint=sb://sangsangplus-eventhubs.servicebus.windows.net/;SharedAccessKeyName=UserConsumerKey;SharedAccessKey=your_key;EntityPath=user-events"

# 애플리케이션 실행
mvn spring-boot:run
```

### 5. Docker 실행

```bash
# Docker 이미지 빌드
docker build -t buildingbite/sangsangplus-product .

# Docker 컨테이너 실행
docker run -p 8082:8082 \
  -e DB_HOST=your_db_host \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e PRODUCT_EVENTHUB_CONNECTION_STRING="your_product_eventhub_connection" \
  -e USER_EVENTHUB_CONNECTION_STRING="your_user_eventhub_connection" \
  buildingbite/sangsangplus-product
```

## ☁️ 배포

### Kubernetes 배포

```bash
# Azure AKS 클러스터 연결
az aks get-credentials --resource-group your-rg --name your-cluster

# 네임스페이스 생성
kubectl apply -f k8s/namespace.yaml

# Secret 설정 (DB 접속 정보만 필요 - JWT 제거됨)
kubectl create secret generic product-service-secret \
  --from-literal=db-username=sangsangplus_admin \
  --from-literal=db-password=your_password \
  -n product-service

# 전체 리소스 배포
kubectl apply -f k8s/
```

### CI/CD 파이프라인

GitHub Actions를 통한 자동 배포가 구성되어 있습니다:

1. **테스트**: Maven 테스트 실행
2. **빌드**: Docker 이미지 빌드 및 푸시 (`buildingbite/sangsangplus-product:latest`)
3. **배포**: Kubernetes 클러스터에 자동 배포
4. **헬스체크**: `/api/products/health` 엔드포인트로 상태 확인

## 🔍 API 사용 예제

### 1. 헬스체크 및 공개 API

```bash
# 헬스체크
curl "https://oauth.buildingbite.com/api/products/health"
# Response: "OK"

# 전체 상품 조회 (페이징)
curl "https://oauth.buildingbite.com/api/products?page=0&size=10&sort=createdAt,desc"

# 특정 상품 조회
curl "https://oauth.buildingbite.com/api/products/1"

# 상품 검색
curl "https://oauth.buildingbite.com/api/products/search?keyword=노트북&page=0&size=20"

# 카테고리별 상품 조회
curl "https://oauth.buildingbite.com/api/products/category/전자제품"

# 특정 사용자 상품 조회 (UUID 사용)
curl "https://oauth.buildingbite.com/api/products/user/550e8400-e29b-41d4-a716-446655440001"

# 최근 상품 조회
curl "https://oauth.buildingbite.com/api/products/recent?limit=5"

# 전체 카테고리 목록
curl "https://oauth.buildingbite.com/api/products/categories"
```

### 2. 인증이 필요한 API (게이트웨이를 통한 호출)

#### 상품 등록

```bash
curl -X POST "https://oauth.buildingbite.com/api/products" \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "MacBook Pro 16인치",
    "description": "2022년 구매, 거의 새 제품",
    "category": "전자제품",
    "price": 2500000,
    "images": [
      {
        "url": "https://example.com/image1.jpg",
        "altText": "MacBook 전면"
      },
      {
        "url": "https://example.com/image2.jpg", 
        "altText": "MacBook 측면"
      }
    ]
  }'
```

#### 상품 수정

```bash
curl -X PUT "https://oauth.buildingbite.com/api/products/1" \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "MacBook Pro 16인치 (가격 인하)",
    "description": "2022년 구매, 거의 새 제품 - 빠른 판매 원함",
    "category": "전자제품",
    "price": 2200000
  }'
```

#### 상품 삭제

```bash
curl -X DELETE "https://oauth.buildingbite.com/api/products/1" \
  -H "Authorization: Bearer your_jwt_token"
```

#### 내 상품 조회

```bash
curl "https://oauth.buildingbite.com/api/products/my?page=0&size=10" \
  -H "Authorization: Bearer your_jwt_token"
```

#### 상품 이미지 추가

```bash
curl -X POST "https://oauth.buildingbite.com/api/products/1/images" \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/new-image.jpg",
    "altText": "추가 상품 이미지"
  }'
```

#### 상품 이미지 삭제

```bash
curl -X DELETE "https://oauth.buildingbite.com/api/products/1/images/5" \
  -H "Authorization: Bearer your_jwt_token"
```

### 3. 로컬 개발환경에서 직접 테스트 (헤더 직접 설정)

```bash
# 상품 등록 (개발용)
curl -X POST "http://localhost:8082/api/products" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440001" \
  -H "X-User-Role: USER" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 상품",
    "description": "로컬 개발환경 테스트",
    "category": "전자제품",
    "price": 10000
  }'

# 내 상품 조회 (개발용)
curl "http://localhost:8082/api/products/my" \
  -H "X-User-Id: 550e8400-e29b-41d4-a716-446655440001" \
  -H "X-User-Role: USER"
```

### 4. 관리자 API

```bash
# 관리자 권한으로 상품 수정
curl -X PUT "https://oauth.buildingbite.com/api/products/admin/1" \
  -H "Authorization: Bearer admin_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "관리자가 수정한 상품",
    "description": "부적절한 내용 수정됨",
    "category": "전자제품",
    "price": 1500000
  }'

# 관리자 권한으로 상품 삭제
curl -X DELETE "https://oauth.buildingbite.com/api/products/admin/1" \
  -H "Authorization: Bearer admin_jwt_token"
```

### 5. 응답 예시

#### 상품 등록 성공 응답 (201 Created)

```json
{
  "productId": 1,
  "userId": "550e8400-e29b-41d4-a716-446655440001",
  "title": "MacBook Pro 16인치",
  "description": "2022년 구매, 거의 새 제품",
  "category": "전자제품",
  "price": 2500000,
  "createdAt": "2024-08-05T04:30:00.000Z",
  "updatedAt": "2024-08-05T04:30:00.000Z",
  "images": [
    {
      "imageId": 1,
      "url": "https://example.com/image1.jpg",
      "altText": "MacBook 전면",
      "displayOrder": 0,
      "createdAt": "2024-08-05T04:30:00.000Z"
    }
  ]
}
```

#### 에러 응답 예시

```json
// 400 Bad Request - 유효성 검증 실패
{
  "timestamp": "2024-08-05T04:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "제목은 필수입니다",
  "path": "/api/products"
}

// 401 Unauthorized - 인증 실패
{
  "timestamp": "2024-08-05T04:30:00.000Z", 
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication required",
  "path": "/api/products"
}

// 403 Forbidden - 권한 부족
{
  "timestamp": "2024-08-05T04:30:00.000Z",
  "status": 403,
  "error": "Forbidden", 
  "message": "You don't have permission to update this product",
  "path": "/api/products/1"
}

// 404 Not Found - 상품 없음
{
  "timestamp": "2024-08-05T04:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

## 📊 모니터링

### Health Check

```bash
# 서비스 상태 확인
curl http://localhost:8082/api/products/health
# Response: "OK"

# Spring Actuator Health
curl http://localhost:8082/actuator/health
```

### Metrics (Prometheus)

```bash
curl http://localhost:8082/actuator/prometheus
```

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │────│ Product Service │────│  PostgreSQL DB  │
│ (JWT → Headers) │    │    (Port 8082)  │    │ (UUID user_id)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                │
                       ┌─────────────────┐
                       │ Azure Event Hub │
                       │ (UUID Events)   │
                       └─────────────────┘
```

### 데이터 플로우

1. **인증**: 게이트웨이에서 JWT 파싱 → X-User-Id, X-User-Role 헤더로 전달
2. **상품 생성**: UUID 사용자 ID로 상품 저장 (사용자 정보는 별도 조회)
3. **이벤트 발행**: 상품 변경 시 Azure Event Hubs로 UUID 이벤트 발행
4. **조회**: 사용자 정보 필요 시 User Service에서 UUID로 조회

## 🧪 테스트

```bash
# 단위 테스트 실행
mvn test

# 통합 테스트 실행 (PostgreSQL 연결 필요)
mvn test -Dspring.profiles.active=test

# 특정 테스트 클래스 실행
mvn test -Dtest=HeaderAuthenticationFilterTest
mvn test -Dtest=ProductLifecycleIntegrationTest
```

### 테스트 데이터

테스트에서 사용되는 UUID:
- **일반 사용자**: `550e8400-e29b-41d4-a716-446655440001`
- **관리자**: `550e8400-e29b-41d4-a716-446655440099`

## 📚 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL (Azure Database)
- **Authentication**: X-User-Id 헤더 기반 (게이트웨이에서 JWT 파싱)
- **Build Tool**: Maven
- **Container**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **Event Streaming**: Azure Event Hubs
- **Monitoring**: Spring Actuator + Prometheus

## 🔄 주요 변경사항

### v2.0.0 (최신)
- **JWT 직접 파싱 제거**: 게이트웨이에서 X-User-Id 헤더로 전달받도록 변경
- **UUID 사용자 ID**: Long → UUID 타입으로 변경
- **사용자 정보 정규화**: user_email, user_name 필드 제거 (user_id만 저장)
- **의존성 간소화**: JWT 라이브러리 제거
- **헬스체크 추가**: `/api/products/health` 엔드포인트 추가

### 마이그레이션 가이드
1. 데이터베이스 백업
2. `scripts/migrate-to-uuid.sql` 실행
3. 게이트웨이에서 X-User-Id, X-User-Role 헤더 전달 확인
4. 새 버전 배포

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing-feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

- **프로젝트 관리자**: SangSangPlus Team
- **이슈 트래킹**: [GitHub Issues](https://github.com/sunflower-class/sangsang-plus-product/issues)