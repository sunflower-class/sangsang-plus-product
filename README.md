# SangSangPlus Product Service

중고 거래 플랫폼 SangSangPlus의 상품 관리 마이크로서비스입니다.

## 📋 개요

- **서비스명**: Product Service
- **포트**: 8082
- **데이터베이스**: Azure PostgreSQL
- **인증**: JWT 토큰 기반 (OAuth 게이트웨이 연동)
- **이벤트**: Azure Event Hubs

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
| GET | `/api/products` | 전체 상품 조회 (페이징) |
| GET | `/api/products/{productId}` | 특정 상품 상세 조회 |
| GET | `/api/products/categories` | 전체 카테고리 목록 |
| GET | `/api/products/recent?limit={limit}` | 최근 상품 조회 |
| GET | `/api/products/search?keyword={keyword}` | 상품 검색 |
| GET | `/api/products/user/{userId}` | 특정 사용자 상품 조회 |
| GET | `/api/products/category/{category}` | 카테고리별 상품 조회 |

### 인증 필요 엔드포인트

| Method | Endpoint | Description | 권한 |
|--------|----------|-------------|------|
| POST | `/api/products` | 상품 등록 | 인증된 사용자 |
| PUT | `/api/products/{productId}` | 상품 수정 | 상품 소유자 |
| DELETE | `/api/products/{productId}` | 상품 삭제 | 상품 소유자 |
| POST | `/api/products/{productId}/images` | 상품 이미지 추가 | 상품 소유자 |
| DELETE | `/api/products/{productId}/images/{imageId}` | 상품 이미지 삭제 | 상품 소유자 |
| GET | `/api/products/my` | 내 상품 조회 | 인증된 사용자 |
| GET | `/api/products/my/category/{category}` | 내 상품 카테고리별 조회 | 인증된 사용자 |

### 관리자 엔드포인트

| Method | Endpoint | Description | 권한 |
|--------|----------|-------------|------|
| PUT | `/api/products/admin/{productId}` | 관리자 상품 수정 | ADMIN |
| DELETE | `/api/products/admin/{productId}` | 관리자 상품 삭제 | ADMIN |

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
| `JWT_ISSUER_URI` | JWT 발행자 URI | `https://oauth.buildingbite.com` |
| `JWT_JWK_SET_URI` | JWT 공개키 세트 URI | `https://oauth.buildingbite.com/.well-known/jwks.json` |
| `JWT_SECRET` | JWT 서명 키 | `your-secret-key-for-jwt-token-must-be-at-least-256-bits` |
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

### 3. 로컬 실행

```bash
# PostgreSQL 연결 정보 설정
export DB_HOST=sangsangplus-postgre-server.postgres.database.azure.com
export DB_USERNAME=sangsangplus_admin
export DB_PASSWORD=your_password

# Event Hubs 연결 정보 설정
export PRODUCT_EVENTHUB_CONNECTION_STRING="Endpoint=sb://sangsangplus-eventhubs.servicebus.windows.net/;SharedAccessKeyName=ProductProducerKey;SharedAccessKey=your_key;EntityPath=product-events"
export USER_EVENTHUB_CONNECTION_STRING="Endpoint=sb://sangsangplus-eventhubs.servicebus.windows.net/;SharedAccessKeyName=UserConsumerKey;SharedAccessKey=your_key;EntityPath=user-events"

# 애플리케이션 실행
mvn spring-boot:run
```

### 4. Docker 실행

```bash
# Docker 이미지 빌드
docker build -t sangsangplus-product .

# Docker 컨테이너 실행
docker run -p 8082:8082 \
  -e DB_HOST=your_db_host \
  -e DB_USERNAME=your_db_user \
  -e DB_PASSWORD=your_db_password \
  -e PRODUCT_EVENTHUB_CONNECTION_STRING="your_product_eventhub_connection" \
  -e USER_EVENTHUB_CONNECTION_STRING="your_user_eventhub_connection" \
  sangsangplus-product
```

## ☁️ 배포

### Kubernetes 배포

```bash
# Azure AKS 클러스터 연결
az aks get-credentials --resource-group your-rg --name your-cluster

# 네임스페이스 생성
kubectl apply -f k8s/namespace.yaml

# Secret 설정 (DB 접속 정보 및 JWT)
kubectl create secret generic product-service-secret \
  --from-literal=db-username=sangsangplus_admin \
  --from-literal=db-password=your_password \
  --from-literal=jwt-secret=YOUR_JWT_SECRET_HERE \
  -n product-service

# 전체 리소스 배포
kubectl apply -f k8s/
```

### CI/CD 파이프라인

GitHub Actions를 통한 자동 배포가 구성되어 있습니다:

1. **테스트**: Maven 테스트 실행
2. **빌드**: Docker 이미지 빌드 및 푸시
3. **배포**: Kubernetes 클러스터에 자동 배포
4. **헬스체크**: 배포된 서비스 상태 확인

## 🔍 API 사용 예제

### 상품 조회

```bash
# 전체 상품 조회 (페이징)
curl "http://localhost:8082/api/products?page=0&size=10"

# 특정 상품 조회
curl "http://localhost:8082/api/products/1"

# 상품 검색
curl "http://localhost:8082/api/products/search?keyword=노트북"
```

### 상품 등록 (인증 필요)

```bash
# Authorization 헤더 사용
curl -X POST "http://localhost:8082/api/products" \
  -H "Authorization: Bearer your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "중고 노트북",
    "description": "사용한지 1년된 노트북입니다",
    "category": "전자제품",
    "price": 800000
  }'

# 쿠키 사용 (access_token)
curl -X POST "http://localhost:8082/api/products" \
  -b "access_token=your_jwt_token" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "중고 노트북",
    "description": "사용한지 1년된 노트북입니다",
    "category": "전자제품",
    "price": 800000
  }'
```

## 📊 모니터링

### Health Check

```bash
curl http://localhost:8082/health
```

### Metrics (Prometheus)

```bash
curl http://localhost:8082/actuator/prometheus
```

## 🏗️ 아키텍처

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │────│ Product Service │────│  PostgreSQL DB  │
│   (OAuth JWT)   │    │    (Port 8082)  │    │    (Azure)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                │
                       ┌─────────────────┐
                       │ Azure Event Hub │
                       │  (Events)       │
                       └─────────────────┘
```

## 🧪 테스트

```bash
# 단위 테스트 실행
mvn test

# 통합 테스트 실행 (PostgreSQL 연결 필요)
mvn test -Dspring.profiles.active=test
```

## 📚 기술 스택

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL (Azure Database)
- **Authentication**: Spring Security + JWT
- **Build Tool**: Maven
- **Container**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **Event Streaming**: Azure Event Hubs
- **Monitoring**: Spring Actuator + Prometheus

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