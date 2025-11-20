-- =============================================================================
-- 테이블 코멘트 적용 검증 스크립트
-- =============================================================================
-- 데이터베이스: careville
-- 작성일: 2025-01-04
-- 설명: table_comments.sql 실행 후 검증용 스크립트
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 코멘트 적용 전/후 비교 체크리스트
-- -----------------------------------------------------------------------------
SET @total_tables = 10;
SET @total_columns = 107;  -- 실제 컬럼 수에 맞게 조정 필요

-- 현재 코멘트 상태 요약
SELECT 
    '테이블 코멘트 상태' AS '구분',
    COUNT(*) AS '전체',
    SUM(CASE WHEN TABLE_COMMENT != '' THEN 1 ELSE 0 END) AS '코멘트있음',
    SUM(CASE WHEN TABLE_COMMENT = '' OR TABLE_COMMENT IS NULL THEN 1 ELSE 0 END) AS '코멘트없음',
    CONCAT(
        ROUND((SUM(CASE WHEN TABLE_COMMENT != '' THEN 1 ELSE 0 END) / COUNT(*)) * 100, 1), 
        '%'
    ) AS '완성률'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )

UNION ALL

SELECT 
    '컬럼 코멘트 상태' AS '구분',
    COUNT(*) AS '전체',
    SUM(CASE WHEN COLUMN_COMMENT != '' THEN 1 ELSE 0 END) AS '코멘트있음',
    SUM(CASE WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN 1 ELSE 0 END) AS '코멘트없음',
    CONCAT(
        ROUND((SUM(CASE WHEN COLUMN_COMMENT != '' THEN 1 ELSE 0 END) / COUNT(*)) * 100, 1), 
        '%'
    ) AS '완성률'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    );

-- -----------------------------------------------------------------------------
-- 2. 테이블별 코멘트 적용 상태 확인
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    CASE 
        WHEN TABLE_COMMENT = '' OR TABLE_COMMENT IS NULL THEN '❌ 미적용'
        ELSE CONCAT('✅ ', TABLE_COMMENT)
    END AS '테이블 코멘트 상태'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
ORDER BY 
    CASE 
        WHEN TABLE_COMMENT = '' OR TABLE_COMMENT IS NULL THEN 0
        ELSE 1
    END,
    TABLE_NAME;

-- -----------------------------------------------------------------------------
-- 3. 필수 코멘트 체크 (주요 컬럼)
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블',
    COLUMN_NAME AS '컬럼',
    COLUMN_KEY AS '키',
    CASE 
        WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN '❌ 코멘트 없음'
        ELSE '✅ OK'
    END AS '상태',
    COLUMN_COMMENT AS '코멘트'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
    AND (
        COLUMN_KEY = 'PRI'  -- Primary Key
        OR COLUMN_NAME LIKE '%_SEQ'  -- 시퀀스 컬럼
        OR COLUMN_NAME LIKE '%_CD'   -- 코드 컬럼
        OR COLUMN_NAME LIKE '%_YN'   -- Y/N 플래그
        OR COLUMN_NAME LIKE '%_DT'   -- 날짜 컬럼
    )
ORDER BY 
    CASE 
        WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN 0
        ELSE 1
    END,
    TABLE_NAME,
    COLUMN_NAME;

-- -----------------------------------------------------------------------------
-- 4. 외래키 참조 관계 코멘트 확인
-- -----------------------------------------------------------------------------
SELECT 
    c.TABLE_NAME AS '테이블',
    c.COLUMN_NAME AS '컬럼',
    c.COLUMN_COMMENT AS '코멘트',
    CASE 
        WHEN c.COLUMN_COMMENT LIKE '%TB_%' OR c.COLUMN_COMMENT LIKE '%참조%' 
        THEN '✅ 참조정보 포함'
        WHEN c.COLUMN_COMMENT != '' 
        THEN '⚠️ 참조정보 미포함'
        ELSE '❌ 코멘트 없음'
    END AS '참조설명 상태'
FROM INFORMATION_SCHEMA.COLUMNS c
WHERE c.TABLE_SCHEMA = DATABASE()
    AND c.TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
    AND (
        c.COLUMN_NAME = 'SERVICE_CD'
        OR c.COLUMN_NAME = 'PRODUCT_NO'
        OR c.COLUMN_NAME = 'FILE_SEQ'
        OR c.COLUMN_NAME = 'FILE_TRGET_SEQ'
    )
ORDER BY c.TABLE_NAME, c.COLUMN_NAME;

-- -----------------------------------------------------------------------------
-- 5. 코멘트 품질 검증 (길이, 특수문자 등)
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블',
    COLUMN_NAME AS '컬럼',
    LENGTH(COLUMN_COMMENT) AS '코멘트길이',
    CASE 
        WHEN LENGTH(COLUMN_COMMENT) < 5 AND COLUMN_COMMENT != '' THEN '⚠️ 너무 짧음'
        WHEN LENGTH(COLUMN_COMMENT) > 100 THEN '⚠️ 너무 김'
        WHEN COLUMN_COMMENT LIKE '%TODO%' OR COLUMN_COMMENT LIKE '%TBD%' THEN '⚠️ 미완성'
        WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN '❌ 없음'
        ELSE '✅ 적절'
    END AS '품질상태',
    COLUMN_COMMENT AS '코멘트'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
    AND (
        LENGTH(COLUMN_COMMENT) < 5 
        OR LENGTH(COLUMN_COMMENT) > 100
        OR COLUMN_COMMENT LIKE '%TODO%'
        OR COLUMN_COMMENT LIKE '%TBD%'
        OR COLUMN_COMMENT = ''
        OR COLUMN_COMMENT IS NULL
    )
ORDER BY 
    CASE 
        WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN 0
        WHEN LENGTH(COLUMN_COMMENT) < 5 THEN 1
        ELSE 2
    END,
    TABLE_NAME,
    COLUMN_NAME;

-- -----------------------------------------------------------------------------
-- 6. 최종 검증 결과 요약
-- -----------------------------------------------------------------------------
SELECT 
    '========== 검증 결과 요약 ==========' AS '구분',
    '' AS '결과'
FROM DUAL

UNION ALL

SELECT 
    '✅ 전체 진행 상황' AS '구분',
    CONCAT(
        '테이블: ', 
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES 
         WHERE TABLE_SCHEMA = DATABASE() 
         AND TABLE_TYPE = 'BASE TABLE'
         AND TABLE_NAME IN (
            'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
            'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
            'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
         )
         AND TABLE_COMMENT != ''),
        '/', @total_tables,
        ' | 컬럼: ',
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = DATABASE()
         AND TABLE_NAME IN (
            'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
            'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
            'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
         )
         AND COLUMN_COMMENT != ''),
        '/', @total_columns
    ) AS '결과'
FROM DUAL

UNION ALL

SELECT 
    CASE 
        WHEN (
            SELECT COUNT(*) 
            FROM INFORMATION_SCHEMA.TABLES 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_TYPE = 'BASE TABLE'
            AND TABLE_NAME IN (
                'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
                'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
                'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
            )
            AND (TABLE_COMMENT = '' OR TABLE_COMMENT IS NULL)
        ) = 0 
        THEN '✅ 모든 테이블 코멘트 적용 완료'
        ELSE '❌ 일부 테이블 코멘트 누락'
    END AS '구분',
    '' AS '결과'
FROM DUAL

UNION ALL

SELECT 
    CASE 
        WHEN (
            SELECT COUNT(*) 
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
            AND TABLE_NAME IN (
                'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
                'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
                'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
            )
            AND (COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL)
        ) = 0 
        THEN '✅ 모든 컬럼 코멘트 적용 완료'
        ELSE CONCAT(
            '⚠️ ', 
            (SELECT COUNT(*) 
             FROM INFORMATION_SCHEMA.COLUMNS
             WHERE TABLE_SCHEMA = DATABASE()
             AND TABLE_NAME IN (
                'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
                'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
                'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
             )
             AND (COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL)),
            '개 컬럼 코멘트 누락'
        )
    END AS '구분',
    '' AS '결과'
FROM DUAL;

-- -----------------------------------------------------------------------------
-- 7. 미적용 항목 상세 리스트 (있는 경우만)
-- -----------------------------------------------------------------------------
-- 코멘트 없는 테이블
SELECT 
    '❌ 코멘트 미적용 테이블:' AS '구분',
    GROUP_CONCAT(TABLE_NAME SEPARATOR ', ') AS '테이블 목록'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
    AND (TABLE_COMMENT = '' OR TABLE_COMMENT IS NULL)
HAVING COUNT(*) > 0;

-- 코멘트 없는 컬럼 (상위 10개만)
SELECT 
    '❌ 코멘트 미적용 컬럼 (상위 10개):' AS '구분',
    CONCAT(TABLE_NAME, '.', COLUMN_NAME) AS '컬럼'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
    AND (COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL)
LIMIT 10;

-- =============================================================================
-- 실행 가이드
-- =============================================================================
-- 1. table_comments.sql 실행 전 이 스크립트를 실행하여 현재 상태 확인
-- 2. table_comments.sql 실행
-- 3. 이 스크립트를 다시 실행하여 적용 결과 확인
-- 4. 모든 항목이 ✅로 표시되면 적용 완료
-- =============================================================================