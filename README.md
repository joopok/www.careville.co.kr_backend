# Careville Review API - Postman 테스트 가이드

## 📋 개요
Careville 리뷰 API의 비밀번호 검증 기능을 테스트하기 위한 Postman 컬렉션입니다.

## 🚀 시작하기

### 1. 애플리케이션 실행
```bash
# Spring Boot 애플리케이션 실행
./mvnw spring-boot:run
# 또는
mvnw.cmd spring-boot:run   # Windows

# 서버 URL: http://localhost:8081
```

### 2. H2 Console 접속 (데이터 확인용)
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비워둠)

### 3. Postman Collection Import
1. Postman을 실행합니다
2. Collections → Import 클릭
3. `Careville_Review_API.postman_collection.json` 파일 선택
4. Import 클릭

## 🔐 테스트 시나리오

### 비밀번호 검증 플로우
```
1. 리뷰 등록 (비밀번호 설정) → 비밀번호 암호화 저장
2. 리뷰 조회 (비밀번호 없이) → 401 Unauthorized
3. 리뷰 조회 (잘못된 비밀번호) → 403 Forbidden  
4. 리뷰 조회 (올바른 비밀번호) → 200 OK
5. 리뷰 수정 (올바른 비밀번호) → success: true
6. 리뷰 수정 (잘못된 비밀번호) → success: false
```

## 📝 API 엔드포인트 및 테스트

### 1. 리뷰 등록
```http
POST /api/reviews
Content-Type: application/json

{
    "reviewNm": "홍길동",
    "reviewCn": "청소 서비스가 정말 깔끔하고 만족스러웠습니다.",
    "starRate": 5,
    "svcCnCd": "001",
    "svcDate": "2024-01-15",
    "pw": "test1234",        // 옵션: 비밀번호 설정
    "dispYn": "Y"
}
```

### 2. 리뷰 조회 (비밀번호 보호)
```http
GET /api/reviews/{reviewSeq}?password=test1234
```

**응답 예시 (성공)**
```json
{
    "success": true,
    "data": {
        "reviewSeq": 1,
        "reviewNm": "홍길동",
        "reviewCn": "청소 서비스가 정말 깔끔하고 만족스러웠습니다.",
        "starRate": 5
    }
}
```

**응답 예시 (비밀번호 필요)**
```json
{
    "success": false,
    "message": "비밀번호가 필요한 리뷰입니다.",
    "requirePassword": true
}
```

### 3. 리뷰 수정
```http
POST /api/reviews/{reviewSeq}
Content-Type: application/json

{
    "pw": "test1234",       // 필수: 비밀번호 검증
    "reviewNm": "홍길동(수정)",
    "reviewCn": "수정된 내용",
    "starRate": 5
}
```

**응답 예시**
```json
{
    "success": true,         // 수정 성공 여부
    "passwordValid": true,   // 비밀번호 검증 결과
    "message": "리뷰가 성공적으로 수정되었습니다."
}
```

### 4. 리뷰 삭제
```http
POST /api/reviews/{reviewSeq}/delete
Content-Type: application/json

{
    "pw": "test1234"        // 필수: 비밀번호 검증
}
```

## 🎯 테스트 케이스

### ✅ 성공 케이스
1. **비밀번호 없는 리뷰 조회/수정/삭제** → 정상 처리
2. **올바른 비밀번호로 조회/수정/삭제** → success: true

### ❌ 실패 케이스
1. **비밀번호 없이 보호된 리뷰 접근** → 401 Unauthorized
2. **잘못된 비밀번호로 접근** → 403 Forbidden
3. **존재하지 않는 리뷰** → 404 Not Found

## 📊 HTTP 상태 코드

| 상태 코드 | 의미 | 설명 |
|---------|------|------|
| 200 OK | 성공 | 조회/수정/삭제 성공 |
| 201 Created | 생성 성공 | 리뷰 등록 성공 |
| 401 Unauthorized | 인증 필요 | 비밀번호가 필요한데 제공되지 않음 |
| 403 Forbidden | 접근 거부 | 잘못된 비밀번호 |
| 404 Not Found | 찾을 수 없음 | 존재하지 않는 리뷰 |
| 400 Bad Request | 잘못된 요청 | 유효성 검증 실패 |

## 🧪 테스트 데이터

### 샘플 비밀번호
- `test1234`: 기본 테스트용 비밀번호
- `admin123`: 관리자 테스트용
- `secret456`: 추가 테스트용

### 샘플 데이터 삽입
```sql
-- sample-data.sql 파일 참조
-- H2 Console에서 실행 가능
```

## 🔍 Postman 테스트 자동화

컬렉션에는 자동 테스트 스크립트가 포함되어 있습니다:

```javascript
// 응답 상태 확인
pm.test("Status code check", function () {
    pm.expect([200, 201, 400, 401, 403, 404]).to.include(pm.response.code);
});

// 비밀번호 검증 확인
if (pm.response.code === 401 || pm.response.code === 403) {
    pm.test("Password validation check", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData.passwordValid).to.eql(false);
    });
}
```

## 💡 유용한 팁

1. **Collection Runner 사용**: 전체 시나리오를 순차적으로 실행
2. **Environment Variables**: 테스트 서버 URL을 변수로 관리
3. **Pre-request Scripts**: 동적 데이터 생성 (타임스탬프 등)
4. **Test Results**: Tests 탭에서 자동 검증 결과 확인

## 📞 문의사항
API 관련 문의사항이 있으시면 개발팀에 연락주세요.




## Project Overview

Careville 청소 서비스 관리 시스템 - A Spring Boot-based admin system for managing cleaning service consultations, case studies, customer communications, product catalog, and customer reviews.

## Build and Run Commands

```bash
# Build the project (Windows)
mvnw.cmd clean package

# Build the project (Linux/Mac)
./mvnw clean package

# Run the application
./mvnw spring-boot:run
# or
java -jar target/cleaning-0.0.1-SNAPSHOT.war

# Run tests
./mvnw test

# Skip tests during build
./mvnw clean package -DskipTests
```

**Note**: The application runs on port 8081 by default.
- Admin Panel: `http://localhost:8081/apage/home.do`
- API Documentation: `http://localhost:8081/swagger-ui.html`
- H2 Console: `http://localhost:8081/h2-console`

## Database Access

H2 Console is available at: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Architecture Overview

### MVC Layer Structure

The application follows a strict layered architecture:

1. **Controllers** (`/ctrl/*.java`): Handle HTTP requests and route to services
   - Admin path pattern: `/apage/{module}0{pageNum}.do` 
   - Page numbers: 10=list, 20=view, 30=register, 40=modify, 51=delete
   - Admin controllers return either Thymeleaf views or "jsonView" for AJAX
   - API controllers (`/api/`) return JSON responses directly

2. **Services** (`/svc/*.java`): Business logic and transaction management
   - Handle pagination via `PageUtil`
   - Manage file relations through `CmmnSvc`
   - Apply AES encryption for sensitive data
   - Password validation using BCrypt for reviews

3. **Mappers** (`/mapper/*.java` + `/resources/db/sqlmap/mappers/*.xml`): MyBatis data access
   - XML-based SQL queries with dynamic conditions
   - Result type: `hashMapCamel` for automatic camelCase conversion

### Core Components Integration

**Session Management**: 
- `SessionCmn` manages all session data through a centralized ConcurrentHashMap
- Login state tracked via `KF_LOGON` key
- Session ID: `LCMS_SESSION`
- Method `isLogon()` checks authentication status

**File Handling**:
- Files stored at path configured in `kframe.filePath`
- `TB_FILE` stores actual files, `TB_FILE_RELATION` links files to entities
- Supports multi-file uploads per consultation/board entry

**Common Code System**:
- `TB_CMMN_CODE` provides dropdown values
- Group codes: 001=서비스내용, 002=상담진행상태

### Key Business Modules

1. **Consultation (상담)**: Customer inquiries with file attachments, status tracking, and admin responses
2. **Case (사례)**: Portfolio management with representative images
3. **Board (게시판)**: Multi-type boards (notice, FAQ, photo) with secret post support
4. **Manager (관리자)**: Admin authentication and access control
5. **Review (리뷰)**: Customer reviews with password protection for modification/deletion
   - Password-protected reviews require validation for viewing/editing
   - Supports star ratings and service type categorization
6. **Category (카테고리)**: Service category management
   - Hierarchical category structure for services
   - Active/inactive status management
7. **Product (상품)**: Product catalog with pricing and categorization
   - Linked to categories via SERVICE_CD
   - Supports discount rates and display order management

### API Endpoints

**Public APIs**:
- `/api/reviews` - Review management (CRUD with password protection)
- `/api/v1/category-products.do` - Combined category and product listing

**Admin Endpoints** (session required):
- `/apage/cnslt0*.do` - Consultation management
- `/apage/case0*.do` - Case study management
- `/apage/board0*.do` - Board management
- `/apage/review0*.do` - Review administration
- `/apage/category0*.do` - Category management
- `/apage/product0*.do` - Product management

### Frontend Architecture

- Thymeleaf templates with layout dialect for consistent UI
- AdminBSB Material Design theme with jQuery/Bootstrap
- AJAX calls return `jsonView` for dynamic updates
- DataTables for list pagination and export features
- Layout structure: `default_layout.html` with header, sidebar, and footer components

### Security Considerations

- AES encryption utility for sensitive data
- BCrypt for password hashing (reviews and manager accounts)
- Session-based authentication for admin panel
- Path-based access control
- CORS configuration for API access
- Password validation with proper HTTP status codes (401/403)

### API Documentation

Swagger/OpenAPI 3.0 documentation available at `/swagger-ui.html`
- Configured in `SwaggerConfig.java`
- Supports multiple server environments (local/production)
- Security schemes for password and session authentication

## Configuration Properties

Key application properties in `application.yml`:
- `kframe.viewRowCnt`: Default pagination size (10)
- `kframe.aes.key`: AES encryption key
- `kframe.filePath`: File upload directory
- `server.port`: 8081
- Database: H2 in-memory with schema/data initialization on startup
- SpringDoc/Swagger enabled at `/api-docs` and `/swagger-ui.html`