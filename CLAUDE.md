# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Careville Cleaning Service Management System** - A Spring Boot 3.5.4 web application for managing customer consultations, case studies, business communications, product catalogs, categories, and reviews. Features an admin panel with authentication, public APIs for reviews, and multi-environment database support.

- **Java Version**: 17
- **Build Tool**: Maven (3.5+)
- **Server Port**: 8081 (development)
- **Database**: H2 in-memory (development), MariaDB (production)
- **Template Engine**: Thymeleaf with layout dialect
- **API Documentation**: Swagger/OpenAPI 3.0 at `/swagger-ui.html`

## Build, Test, and Run Commands

### Building
```bash
# Clean build (macOS/Linux)
./mvnw clean package

# Build skipping tests
./mvnw clean package -DskipTests

# Windows
mvnw.cmd clean package
```

### Running
```bash
# Run application via Maven
./mvnw spring-boot:run

# Run via built WAR
java -jar target/cleaning-0.0.1-SNAPSHOT.war

# Run tests
./mvnw test

# Run single test
./mvnw test -Dtest=ClassName
```

### Development Endpoints
- Application: `http://localhost:8081/apage/home.do`
- H2 Console: `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:testdb`, user: `sa`, password: empty)
- Swagger API docs: `http://localhost:8081/swagger-ui.html`

### Database Initialization
Database is auto-initialized on startup via `application.yml` configuration:
- Schema: `src/main/resources/db/schema.sql` + `schema-*.sql` files
- Data: `src/main/resources/db/data*.sql` + `data-*.sql` files

For production MariaDB setup, use provided SQL scripts:
- `schema_mariadb.sql` - Table definitions
- `data_mariadb.sql` - Initial data
- `table_comments.sql`, `comment_utilities.sql`, `comment_verification.sql` - Documentation/utilities

## Code Architecture

### Layered Structure
```
src/main/java/kr/co/cleaning/
├── CleaningApplication.java          # @SpringBootApplication entry point
├── ServletInitializer.java           # WAR deployment initialization
├── ctrl/                             # MVC Controllers (HTTP request handlers)
│   ├── *Ctrl.java                    # Admin controllers (Thymeleaf views)
│   └── *ApiCtrl.java                 # API controllers (JSON responses)
├── svc/                              # Service layer (business logic, transactions)
│   └── *Svc.java
├── mapper/                           # MyBatis data access interfaces
│   └── *Mapper.java
└── core/
    ├── config/                       # Spring configuration & utilities
    │   ├── SessionCmn.java          # Session management (ConcurrentHashMap-based)
    │   ├── SwaggerConfig.java       # Swagger/OpenAPI configuration
    │   ├── CorsConfig.java          # CORS for API access
    │   ├── KFExceptionResolver.java # Custom exception handling
    │   ├── JsonView.java            # JSON response rendering
    │   ├── HashMapCamel.java        # MyBatis camelCase result mapping
    │   └── BaseConfig.java          # Application base configuration
    └── utils/
        ├── FileUtil.java            # File upload/download handling
        ├── PageUtil.java            # Pagination calculation
        ├── AESUtil.java             # AES encryption for sensitive data
        └── SUtils.java              # String & utility methods
```

### Request Routing Patterns

**Admin Controllers** (session-required, Thymeleaf views):
- `/apage/{module}010.do` → List view with pagination
- `/apage/{module}020.do` → Detail/view page
- `/apage/{module}030.do` → Register/insert form
- `/apage/{module}040.do` → Modify/update form
- `/apage/{module}051.do` → Delete operation

Examples:
- `/apage/cnslt010.do` - Consultation list
- `/apage/board040.do` - Board modify form
- `/apage/product020.do` - Product detail

**API Controllers** (public JSON endpoints):
- `/api/reviews` - Review CRUD with password protection
- `/api/v1/category-products.do` - Combined category/product listing

AJAX calls from admin return `jsonView` for dynamic updates instead of full page renders.

### Core Components Integration

**Session Management** (`SessionCmn`):
- Centralized session storage via `ConcurrentHashMap`
- Session key: `LCMS_SESSION` (configured in `kframe.session-id`)
- Logon state key: `KF_LOGON` (configured in `kframe.session-log-in-key`)
- Check auth: `sessionCmn.isLogon()` returns boolean
- Inject: Use constructor injection of `HttpSession`, instantiate `SessionCmn(session)`

**File Handling**:
- Stored at: `kframe.filePath` (configured in `application.yml`, default: `C:/Users/nun/Downloads/careville`)
- Database tables: `TB_FILE` (actual files), `TB_FILE_RELATION` (linking to entities)
- Managed by: `CmmnSvc` for upload/download/relation management
- Supports multi-file uploads per consultation/board/case

**Common Code System** (`TB_CMMN_CODE`):
- Dropdown values from database
- Group codes: `001` = Service content (서비스내용), `002` = Consultation status (상담진행상태)
- Loaded in services via `CmmnSvc`

**Encryption**:
- AES encryption via `AESUtil` for sensitive data (key in `kframe.aes.key`)
- BCrypt for password hashing (reviews & manager accounts, dependency: jbcrypt)

### MyBatis Data Access

**Configuration**:
- Mapper XML location: `src/main/resources/db/sqlmap/mappers/*.xml`
- Config file: `src/main/resources/db/sqlmap/sql-mapper-config.xml`
- Result mapping: All mappers use `resultType="hashMapCamel"` for automatic camelCase conversion
- Dynamic SQL supported via `<if>`, `<foreach>`, `<choose>` elements

**Mapper-Service Relationship**:
- Each `*Mapper.java` interface has corresponding `*Mapper.xml` file
- Services call mapper methods and apply business logic/pagination via `PageUtil`
- Example: `ReviewMapper.selectReviewList()` + `PageUtil.getPageInfo()` → paginated results

### Key Business Modules

1. **Consultation (상담)**: Customer inquiries with file attachments, status tracking, admin responses
2. **Case (사례)**: Portfolio/showcase management with representative images
3. **Board (게시판)**: Multi-type boards (notice=01, FAQ=02, photo=03) with secret post support
4. **Manager (관리자)**: Admin user authentication and access control
5. **Review (리뷰)**: Customer reviews with BCrypt password protection
   - View/edit/delete operations require password validation
   - Star ratings (1-5) and service type categorization
   - API at `/api/reviews` with proper HTTP status codes (401 for missing password, 403 for invalid)
6. **Category (카테고리)**: Service category hierarchy with active/inactive status
7. **Product (상품)**: Product catalog with pricing, categorization, discount rates, display order

### Frontend Architecture

- **Template Engine**: Thymeleaf with layout dialect (`thymeleaf-layout-dialect`)
- **Layout Template**: `default_layout.html` (header, sidebar, footer components)
- **UI Theme**: AdminBSB Material Design
- **Dependencies**: jQuery, Bootstrap, DataTables (for list pagination & export)
- **Dynamic Updates**: AJAX calls with `jsonView` response type for partial page updates

## Configuration Properties

Key properties in `application.yml`:

```yaml
kframe:
  viewRowCnt: 10           # Default pagination size
  viewPageCnt: 5           # Page numbers to display in pagination
  session-id: LCMS_SESSION # Session map key
  session-log-in-key: KF_LOGON  # Logon info key
  aes:
    key: my-secret-cleaning-12345  # AES encryption key
    algorithm: AES
  filePath: C:/Users/nun/Downloads/careville  # File upload directory

server:
  port: 8081
  servlet:
    multipart:
      maxFileSize: 10MB        # Individual file max size
      maxRequestSize: 30MB     # Total request max size
```

## Security Considerations

- **Session-Based Auth**: Admin panel protected via `SessionCmn.isLogon()` checks
- **Password Protection**: Reviews use BCrypt; review access/modification requires password validation
- **HTTP Status Codes**: 401 (missing password), 403 (invalid password), 200 (success)
- **Path-Based Access Control**: Admin endpoints require session; public API endpoints are open
- **AES Encryption**: Sensitive data encrypted with utility key
- **CORS**: Configured via `CorsConfig` for API access
- **Swagger Security Schemes**: Configured for password and session authentication in `SwaggerConfig`

## Testing

- Test location: `src/test/java/kr/co/cleaning/`
- Currently minimal: `CleaningApplicationTests.java`
- Run: `./mvnw test` or `./mvnw test -Dtest=CleaningApplicationTests`

## Common Development Tasks

### Adding a New Admin Module

1. **Create Controller** (`ctrl/NewCtrl.java`):
   - Extend with route handlers for `010/020/030/040/051.do` pages
   - Inject `SessionCmn` for auth checks via `@RequiredArgsConstructor`

2. **Create Service** (`svc/NewSvc.java`):
   - Implement business logic with `@Transactional` for DB operations
   - Use `PageUtil` for pagination

3. **Create Mapper**:
   - Interface: `mapper/NewMapper.java`
   - XML: `db/sqlmap/mappers/NewMapper.xml` with dynamic SQL queries

4. **Create Thymeleaf Views**:
   - Location: `src/main/resources/templates/`
   - Use layout dialect inheritance from `default_layout.html`

### Adding an API Endpoint

1. **Create Controller** (`ctrl/NewApiCtrl.java`):
   - Use `@RestController` with `@RequestMapping("/api/...")`
   - Return DTOs/Map objects (auto-converted to JSON)

2. **Create Service** with business logic
3. **Create Mapper** for database access
4. **Document in Swagger**: Use `@Operation`, `@ApiResponse` annotations

### Password Protection Pattern (Reviews Example)

- Password field stored as BCrypt hash via `BCryptPasswordEncoder`
- View/edit/delete check: `ReviewSvc.validatePassword(password, hashedPassword)` returns boolean
- API returns JSON with `passwordValid`, `success`, `requirePassword` fields
- Controller returns appropriate HTTP status codes based on validation result

## Dependencies & Libraries

Key Maven dependencies (see `pom.xml`):

| Dependency | Purpose |
|------------|---------|
| spring-boot-starter-web | Web application framework |
| spring-boot-starter-thymeleaf | Template rendering |
| thymeleaf-layout-dialect | Template inheritance |
| mybatis-spring-boot-starter | Database access |
| h2database | In-memory DB for dev/testing |
| jbcrypt | BCrypt password hashing |
| log4jdbc-log4j2 | SQL query logging |
| spring-boot-starter-aop | Aspect-oriented programming |
| springdoc-openapi-starter-webmvc-ui | Swagger/OpenAPI documentation |

## Logging Configuration

Logging controlled via `application.yml`:

```yaml
logging:
  level:
    root: INFO
    jdbc.sqltiming: OFF  # Change to DEBUG to see SQL execution times
    kr.co.cleaning: INFO # Application logging
```

SQL logging available via `log4jdbc` when enabled; database operations use this for query logging.

## Key Files & Directories

- `src/main/resources/application.yml` - Main configuration (port, DB, pagination, encryption key, file path)
- `src/main/resources/db/schema.sql` - Table definitions
- `src/main/resources/db/data_cmmn.sql` - Common code data
- `src/main/resources/db/sqlmap/sql-mapper-config.xml` - MyBatis configuration
- `src/main/resources/templates/` - Thymeleaf views
- `src/main/resources/static/` - CSS, JS, images (AdminBSB theme + plugins)
- `.mvn/wrapper/` - Maven wrapper scripts
- `.env` - Environment variables (add to `.gitignore`, never commit)

## IDE Configuration Notes

- **Java Version**: Set to 17 in IDE
- **Language Level**: Java 17
- **Build Tool**: Maven (configured via `.mvn/wrapper`)
- **MyBatis Plugin** (optional): Helps with mapper-XML navigation
- **Thymeleaf Plugin** (optional): Better template editing

---

# Cafe24 호스팅 환경 가이드

## 호스팅 서버 정보

### 서버 환경
| 항목 | 값 |
|------|-----|
| **호스트** | ksm1779.cafe24.com |
| **서버명** | umj7-022 |
| **IP 주소** | 210.114.6.195 |
| **Tomcat** | 10.0.x |
| **JDK** | 17 ✅ |
| **Servlet** | 5.0 |
| **JSP** | 3.0 |
| **Database** | MariaDB 10.1.x (UTF-8) |

### 용량 정보
| 항목 | 용량 |
|------|------|
| **웹 용량** | 1000MB (1GB) |
| **웹 트래픽** | 1500MB/월 |
| **스트리밍 용량** | 500MB (미신청) |
| **CDN 용량** | 500MB (미신청) |

### 호환성 확인
✅ **JDK 17** - 프로젝트와 완벽 호환
✅ **Tomcat 10.0.x** - Spring Boot 3.5.4 호환
✅ **Servlet 5.0** - Jakarta EE 9+ 호환
✅ **MariaDB 10.1.x** - `schema_mariadb.sql` 사용 가능

### 주의사항
⚠️ **웹 용량 1GB 제한**: WAR 파일(약 100-150MB) + 업로드 파일 관리 필요
⚠️ **트래픽 1.5GB/월**: 파일 다운로드/이미지 최적화 필요
⚠️ **메모리 제한**: Tomcat 메모리 설정 확인 필요

## 배포 환경 설정

### 접속 정보 (.env 파일에 저장)

```bash
# .env (add to .gitignore)
HOSTING_HOST=ksm1779.cafe24.com
HOSTING_IP=210.114.6.195
HOSTING_USER=ksm1779

# Database 설정
DB_USERNAME=ksm1779
DB_PASSWORD=Chae1030!!
DATABASE_NAME=ksm1779

# SSH/FTP 접속 시 필요
# 비밀번호는 `.env` 파일에만 저장, git에 커밋하지 않음
```

### MariaDB 설정

| 항목 | 값 |
|------|-----|
| **데이터베이스명** | ksm1779 |
| **사용자명** | ksm1779 |
| **Charset** | UTF-8 (utf8mb4) |
| **Collation** | utf8mb4_unicode_ci |

**application.yml** (MariaDB 설정):
```yaml
spring:
  datasource:
    driver-class-name: net.sf.log4jdbc.sql.jdbcapi.DriverSpy
    url: jdbc:log4jdbc:mysql://localhost:3306/ksm1779?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: ${DB_USERNAME:ksm1779}
    password: ${DB_PASSWORD:Chae1030!!}
```

**application-prod.yml** (운영 환경):
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ksm1779?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: ${DB_USERNAME:ksm1779}
    password: ${DB_PASSWORD:Chae1030!!}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
```

### FTP/SSH 접속 정보

| 항목 | 값 |
|------|-----|
| **FTP/SSH 주소** | ksm1779.cafe24.com |
| **FTP/SSH 아이디** | ksm1779 |
| **FTP 포트** | 21 |
| **SSH 포트** | 22 |

### 접속 방법

**SSH 접속**:
```bash
ssh -p 22 ksm1779@ksm1779.cafe24.com
# 또는
ssh -p 22 ksm1779@210.114.6.195
```

**FTP 접속**:
```bash
# 명령줄 FTP
ftp -p 21 ksm1779.cafe24.com

# 또는 FTP 클라이언트 사용
# - FileZilla
# - WinSCP
# - Transmit
```

**FTP 클라이언트 설정**:
- Host: ksm1779.cafe24.com
- Port: 21
- Username: ksm1779
- Password: [FTP 비밀번호]
- Protocol: FTP (또는 SFTP for SSH)

### MariaDB 접속 정보

| 항목 | 값 |
|------|-----|
| **DB 주소** | localhost |
| **DB 포트** | 3306 |
| **DB 아이디** | ksm1779 |
| **DB 종류** | MariaDB |
| **Database** | ksm1779 |
| **Charset** | utf8mb4 |

**MySQL/MariaDB 클라이언트 접속**:
```bash
mysql -h localhost -P 3306 -u ksm1779 -p
# 비밀번호 입력: Chae1030!!
```

**SSH를 통한 원격 접속**:
```bash
# SSH 터널링을 통한 원격 DB 접속
ssh -L 3306:localhost:3306 ksm1779@ksm1779.cafe24.com

# 다른 터미널에서
mysql -h 127.0.0.1 -P 3306 -u ksm1779 -p
```

## 배포 절차

### 1. 배포 전 준비사항

**application.yml 수정 (운영 환경용)**:

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/careville?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # 운영에서는 validate로 설정
  h2:
    console:
      enabled: false  # 운영에서는 비활성화

server:
  port: 8080  # Tomcat의 실제 포트
  servlet:
    context-path: /  # 필요시 서브 경로 설정

kframe:
  filePath: /home/ksm1779/careville/files  # 운영 서버의 파일 경로
  aes:
    key: ${AES_ENCRYPTION_KEY}  # 환경 변수로 관리

logging:
  level:
    root: WARN
    kr.co.cleaning: INFO
```

### 2. WAR 빌드

```bash
./mvnw clean package -DskipTests
# Output: target/cleaning-0.0.1-SNAPSHOT.war
```

### 3. 데이터베이스 마이그레이션

SSH로 접속 후:
```bash
# MariaDB 접속
mysql -u [username] -p

# SQL 스크립트 실행
source schema_mariadb.sql
source data_mariadb.sql
```

또는 Cafe24 관리 콘솔의 PhpMyAdmin 사용

### 4. 파일 배포

**FTP로 배포**:
1. FTP 클라이언트로 접속
2. WAR 파일을 `/home/ksm1779/tomcat/webapps/` 또는 지정된 경로에 업로드
3. Tomcat 재시작 (Cafe24 관리 콘솔)

**SSH로 배포**:
```bash
scp target/cleaning-0.0.1-SNAPSHOT.war ksm1779@ksm1779.cafe24.com:/path/to/deploy/
```

### 5. 파일 업로드 디렉토리 생성

```bash
ssh ksm1779@ksm1779.cafe24.com

# 파일 저장 디렉토리 생성
mkdir -p /home/ksm1779/careville/files
chmod 755 /home/ksm1779/careville/files
```

### 6. Tomcat 재시작

Cafe24 관리 콘솔 또는 SSH로:
```bash
# Cafe24 콘솔에서 "Tomcat 재시작" 버튼 클릭
# 또는 SSH로 직접 실행 (관리 권한 필요)
```

## 배포 후 확인 사항

- ✅ Admin panel: `https://ksm1779.cafe24.com/apage/home.do`
- ✅ Swagger API: `https://ksm1779.cafe24.com/swagger-ui.html`
- ✅ Public API: `https://ksm1779.cafe24.com/api/reviews`
- ✅ 파일 업로드/다운로드 기능 테스트
- ✅ 로그 확인 (Cafe24 콘솔 또는 SSH)
- ✅ 데이터베이스 연결 확인

## Cafe24 관리 콘솔 주요 메뉴

- **호스팅 관리**: https://hosting.cafe24.com/?controller=myservice_hosting_main
- Tomcat 상태 확인 및 재시작
- 용량 모니터링
- 로그 파일 조회
- SSL 인증서 관리
- 도메인 설정

## 트러블슈팅

### 배포 후 503 Service Unavailable

- SSH로 접속하여 Tomcat 로그 확인
- Tomcat 메모리 설정 확인
- WAR 파일 손상 여부 확인 (재배포)

### 데이터베이스 연결 실패

- MariaDB 사용자명/비밀번호 확인 (`.env` 파일)
- 데이터베이스 호스트 주소 확인
- 방화벽 설정 확인

### 파일 업로드 실패

- 파일 저장 디렉토리 권한 확인
- 디스크 용량 확인 (1GB 제한)
- `kframe.filePath` 설정 확인

### 로그 위치
- Tomcat 로그: Cafe24 콘솔의 로그 조회 메뉴
- 애플리케이션 로그: Tomcat 로그 출력 (설정된 로깅 레벨에 따름)

---

# Tomcat 서버 배포 및 API 호출 가이드

## 프로젝트 배포 아키텍처

현재 프로젝트는 **외부 Tomcat 서버에 배포**되는 WAR 구조입니다:

```
개발 환경                          운영 환경 (Cafe24)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
./mvnw spring-boot:run      →  Tomcat 10.0.x
├─ 내장 Tomcat (8081)           ├─ 외부 Tomcat
├─ H2 Database                 ├─ MariaDB 10.1.x
└─ 로컬 테스트                    └─ 운영 환경
```

### WAR 패키징 설정

**pom.xml**에서:
```xml
<packaging>war</packaging>  <!-- 외부 Tomcat 배포용 -->
```

**ServletInitializer.java** (외부 Tomcat 초기화):
```java
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CleaningApplication.class);
    }
}
```

**Tomcat 의존성**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>  <!-- 외부 Tomcat 사용 -->
</dependency>
```

## API 엔드포인트 접근

### 개발 환경 (로컬)

```
Base URL: http://localhost:8081

공개 API:
- GET  /api/reviews                    → 리뷰 목록 조회
- GET  /api/reviews/{reviewSeq}        → 리뷰 상세 조회
- POST /api/reviews                    → 리뷰 등록
- POST /api/reviews/{reviewSeq}        → 리뷰 수정
- POST /api/reviews/{reviewSeq}/delete → 리뷰 삭제
- GET  /api/v1/category-products.do    → 카테고리+상품 목록

관리자 패널:
- GET  /apage/home.do                  → 관리자 홈
- GET  /apage/cnslt010.do              → 상담 목록
- GET  /apage/review010.do             → 리뷰 관리
- ...

API 문서:
- GET  /swagger-ui.html                → Swagger 문서
- GET  /api-docs                       → OpenAPI JSON
```

### 운영 환경 (Cafe24 Tomcat)

```
Base URL: https://ksm1779.cafe24.com  (또는 https://210.114.6.195)

공개 API:
- GET  /api/reviews
- GET  /api/reviews/{reviewSeq}
- POST /api/reviews
- ...

관리자 패널:
- https://ksm1779.cafe24.com/apage/home.do

API 문서:
- https://ksm1779.cafe24.com/swagger-ui.html
```

## Context Path 설정

### WAR 파일명에 따른 경로 결정

| WAR 파일명 | Context Path | API 접근 경로 |
|-----------|--------------|--------------|
| `ROOT.war` | `/` | `/api/reviews` |
| `cleaning.war` | `/cleaning` | `/cleaning/api/reviews` |
| `app-v1.war` | `/app-v1` | `/app-v1/api/reviews` |

**Cafe24에서 Context Path 설정**:
- 기본적으로 WAR 파일명이 Context Path가 됨
- ROOT 경로로 배포하려면 `ROOT.war`로 이름 변경 필요

## 프론트엔드에서 API 호출

### JavaScript (fetch API)

```javascript
// 개발 환경
const API_BASE = 'http://localhost:8081/api';

// 운영 환경
// const API_BASE = 'https://ksm1779.cafe24.com/api';

// 리뷰 목록 조회
async function getReviews() {
    try {
        const response = await fetch(`${API_BASE}/reviews`);
        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('API Error:', error);
    }
}

// 리뷰 상세 조회 (비밀번호 보호)
async function getReview(reviewSeq, password) {
    try {
        const response = await fetch(`${API_BASE}/reviews/${reviewSeq}?password=${password}`);
        if (response.status === 401) {
            alert('비밀번호가 필요합니다');
            return;
        }
        if (response.status === 403) {
            alert('잘못된 비밀번호입니다');
            return;
        }
        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('API Error:', error);
    }
}

// 리뷰 등록
async function createReview(reviewData) {
    try {
        const response = await fetch(`${API_BASE}/reviews`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                reviewNm: reviewData.name,
                reviewCn: reviewData.content,
                starRate: reviewData.rating,
                svcCnCd: reviewData.serviceCode,
                svcDate: reviewData.serviceDate,
                pw: reviewData.password,  // 옵션
                dispYn: 'Y'
            })
        });
        const data = await response.json();
        console.log('Review created:', data);
    } catch (error) {
        console.error('API Error:', error);
    }
}

// 카테고리 + 상품 조회
async function getCategoryProducts() {
    try {
        const response = await fetch(`${API_BASE}/v1/category-products.do`);
        const data = await response.json();
        console.log(data);
    } catch (error) {
        console.error('API Error:', error);
    }
}
```

### Axios 라이브러리

```javascript
import axios from 'axios';

const API_BASE = 'http://localhost:8081/api';

const apiClient = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 리뷰 목록
export async function getReviews() {
    return apiClient.get('/reviews');
}

// 리뷰 상세 (비밀번호 포함)
export async function getReview(reviewSeq, password) {
    try {
        return await apiClient.get(`/reviews/${reviewSeq}`, {
            params: { password }
        });
    } catch (error) {
        if (error.response?.status === 401) {
            throw new Error('비밀번호가 필요합니다');
        } else if (error.response?.status === 403) {
            throw new Error('잘못된 비밀번호입니다');
        }
        throw error;
    }
}

// 리뷰 등록
export async function createReview(reviewData) {
    return apiClient.post('/reviews', reviewData);
}

// 리뷰 수정
export async function updateReview(reviewSeq, password, updateData) {
    return apiClient.post(`/reviews/${reviewSeq}`, {
        pw: password,
        ...updateData
    });
}

// 리뷰 삭제
export async function deleteReview(reviewSeq, password) {
    return apiClient.post(`/reviews/${reviewSeq}/delete`, {
        pw: password
    });
}

// 카테고리 + 상품
export async function getCategoryProducts() {
    return apiClient.get('/v1/category-products.do');
}
```

## CORS 설정 확인

프론트엔드가 다른 도메인에서 API를 호출할 때 CORS 설정 필요:

**CorsConfig.java** (프로젝트에 기존 포함):
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")  // 필요시 특정 도메인으로 제한
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
```

## 환경별 API 기본 URL 관리

### 환경 변수 사용 (권장)

```javascript
// 환경 변수로 관리
const API_BASE = process.env.REACT_APP_API_BASE || 'http://localhost:8081/api';

// 또는 Vite
const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8081/api';
```

**.env 파일 (개발)**:
```
REACT_APP_API_BASE=http://localhost:8081/api
```

**.env.production 파일 (운영)**:
```
REACT_APP_API_BASE=https://ksm1779.cafe24.com/api
```

### 동적 URL 설정

```javascript
// 현재 도메인 기반으로 자동 결정
function getApiBase() {
    if (window.location.hostname === 'localhost') {
        return 'http://localhost:8081/api';
    } else {
        // 현재 도메인의 API 사용
        return '/api';
    }
}

const API_BASE = getApiBase();
```

## API 테스트

### cURL로 테스트

```bash
# 개발 환경
curl http://localhost:8081/api/reviews

# 운영 환경
curl https://ksm1779.cafe24.com/api/reviews

# 비밀번호로 보호된 리뷰 조회
curl "http://localhost:8081/api/reviews/1?password=test1234"

# 리뷰 등록
curl -X POST http://localhost:8081/api/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "reviewNm": "테스트",
    "reviewCn": "좋습니다",
    "starRate": 5,
    "svcCnCd": "001",
    "svcDate": "2024-01-15",
    "pw": "test1234",
    "dispYn": "Y"
  }'
```

### Postman으로 테스트

1. Postman 실행
2. `POST http://localhost:8081/api/reviews`
3. Body → Raw → JSON 선택
4. 요청 데이터 입력 후 Send

### Swagger UI로 테스트

1. `http://localhost:8081/swagger-ui.html` 접속
2. API 엔드포인트 확장
3. "Try it out" 버튼 클릭
4. 파라미터 입력 후 Execute

## Tomcat 배포 후 API 호출 검증

배포 후 다음과 같이 확인:

```bash
# 1. 응답 상태 확인
curl -I https://ksm1779.cafe24.com/api/reviews

# 2. 실제 API 응답 확인
curl https://ksm1779.cafe24.com/api/reviews

# 3. Swagger 문서 확인
curl https://ksm1779.cafe24.com/swagger-ui.html

# 4. CORS 헤더 확인 (프론트엔드 통신 확인)
curl -H "Origin: https://example.com" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS https://ksm1779.cafe24.com/api/reviews -v
```

---

## Notes for Future Development

- File path in `application.yml` needs adjustment per environment (currently hardcoded Windows path)
- H2 console password is empty in dev mode (security: only enable in development)
- Database initialization happens on every startup (schema + data scripts in `application.yml`)
- Review password validation uses BCrypt; always validate before update/delete operations
- Session is ConcurrentHashMap-based for thread safety; safe for multi-user scenarios
- AES encryption key should be externalized to environment variables for production
- Cafe24 hosting: Verify supported Java version and Tomcat configuration before deployment
- Monitor application logs via Cafe24 control panel after deployment
- WAR 파일이 1GB 제한에 포함되므로, 파일 업로드 용량 관리 필수
- 월 1.5GB 트래픽 제한: 이미지 최적화, CDN 사용 고려
- API 호출 시 Context Path 확인 필수 (ROOT.war vs cleaning.war)
- CORS 설정으로 인한 API 호출 실패 시 CorsConfig.java 확인
- 프론트엔드 환경변수로 API_BASE 관리하여 개발/운영 환경 자동 전환