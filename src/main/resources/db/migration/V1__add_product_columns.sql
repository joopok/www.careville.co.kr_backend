-- =============================================================================
-- TB_PRODUCT 테이블 컬럼 추가 마이그레이션 (MariaDB 10.1.x)
-- =============================================================================
-- 실행 방법:
-- 1. Cafe24 phpMyAdmin 또는 SSH로 MariaDB 접속
-- 2. 이 스크립트를 전체 실행
-- =============================================================================

-- 트랜잭션 시작
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- 컬럼 존재 여부 확인 후 추가 (MariaDB 10.1.x 호환)
-- 참고: MariaDB 10.1.x는 IF NOT EXISTS를 ADD COLUMN에서 지원하지 않을 수 있음
-- 에러 발생 시 해당 컬럼 이미 존재하는 것이므로 다음 명령 진행

-- 간단 설명 컬럼 추가
ALTER TABLE TB_PRODUCT ADD COLUMN SHORT_DESC VARCHAR(500) COMMENT '간단 설명' AFTER PRODUCT_NM;

-- 특징 리스트 컬럼 추가 (JSON)
ALTER TABLE TB_PRODUCT ADD COLUMN FEATURES LONGTEXT COMMENT '특징 리스트(JSON)' AFTER SERVICE_INCLUDES;

-- 서비스 효과 컬럼 추가 (JSON)
ALTER TABLE TB_PRODUCT ADD COLUMN SERVICE_EFFECTS LONGTEXT COMMENT '서비스 효과(JSON)' AFTER FEATURES;

-- 작업 과정 컬럼 추가 (JSON)
ALTER TABLE TB_PRODUCT ADD COLUMN WORK_PROCESS LONGTEXT COMMENT '작업 과정(JSON)' AFTER SERVICE_EFFECTS;

-- 인기 배지 여부 컬럼 추가
ALTER TABLE TB_PRODUCT ADD COLUMN POPULAR_YN VARCHAR(1) DEFAULT 'N' COMMENT '인기 배지 여부' AFTER WORK_PROCESS;

-- 대표 이미지 컬럼 추가
ALTER TABLE TB_PRODUCT ADD COLUMN THUMBNAIL_SEQ INT COMMENT '대표 이미지(TB_FILE)' AFTER POPULAR_YN;

-- 트랜잭션 커밋
COMMIT;
SET AUTOCOMMIT = 1;

-- 확인 쿼리
DESCRIBE TB_PRODUCT;

-- =============================================================================
-- 샘플 데이터 업데이트 (선택사항)
-- =============================================================================
-- 기존 상품에 샘플 데이터 추가
UPDATE TB_PRODUCT SET
    SHORT_DESC = '전문 분해 청소로 새것처럼',
    FEATURES = '["완전 분해청소","살균소독","냄새제거"]',
    SERVICE_EFFECTS = '["시원한 바람","냄새 제거","전력 효율 향상"]',
    WORK_PROCESS = '["예약확인","방문","분해청소","조립","시운전"]',
    POPULAR_YN = 'Y'
WHERE PRODUCT_NO = 1;

UPDATE TB_PRODUCT SET
    SHORT_DESC = '2대 동시 청소로 더욱 저렴하게',
    FEATURES = '["2대 동시청소","필터 교체","1년 보증"]',
    POPULAR_YN = 'N'
WHERE PRODUCT_NO = 2;

-- =============================================================================
-- 마이그레이션 완료
-- =============================================================================
