-- =============================================================================
-- 테이블 코멘트 관리 유틸리티 스크립트
-- =============================================================================
-- 데이터베이스: careville
-- 작성일: 2025-01-04
-- 설명: 테이블 및 컬럼 코멘트 조회/검증을 위한 유틸리티 쿼리 모음
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 1. 전체 테이블 코멘트 조회
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    TABLE_COMMENT AS '테이블 설명',
    TABLE_ROWS AS '레코드수',
    CREATE_TIME AS '생성일시'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_NAME;

-- -----------------------------------------------------------------------------
-- 2. 특정 테이블의 컬럼 코멘트 상세 조회
-- -----------------------------------------------------------------------------
-- TB_CONSULTATION 테이블 예시
SELECT 
    ORDINAL_POSITION AS '순번',
    COLUMN_NAME AS '컬럼명',
    COLUMN_TYPE AS '데이터타입',
    IS_NULLABLE AS 'NULL허용',
    COLUMN_KEY AS '키타입',
    COLUMN_DEFAULT AS '기본값',
    COLUMN_COMMENT AS '설명'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'TB_CONSULTATION'
ORDER BY ORDINAL_POSITION;

-- -----------------------------------------------------------------------------
-- 3. 코멘트가 없는 테이블 찾기
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    'No Comment' AS '상태'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
    AND (TABLE_COMMENT IS NULL OR TABLE_COMMENT = '')
ORDER BY TABLE_NAME;

-- -----------------------------------------------------------------------------
-- 4. 코멘트가 없는 컬럼 찾기
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    COLUMN_NAME AS '컬럼명',
    COLUMN_TYPE AS '데이터타입',
    'No Comment' AS '상태'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND (COLUMN_COMMENT IS NULL OR COLUMN_COMMENT = '')
    AND TABLE_NAME IN (
        SELECT TABLE_NAME 
        FROM INFORMATION_SCHEMA.TABLES 
        WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_TYPE = 'BASE TABLE'
    )
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- -----------------------------------------------------------------------------
-- 5. 테이블별 컬럼 코멘트 완성도 통계
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    COUNT(*) AS '전체컬럼수',
    SUM(CASE WHEN COLUMN_COMMENT != '' THEN 1 ELSE 0 END) AS '코멘트있음',
    SUM(CASE WHEN COLUMN_COMMENT = '' OR COLUMN_COMMENT IS NULL THEN 1 ELSE 0 END) AS '코멘트없음',
    CONCAT(
        ROUND(
            (SUM(CASE WHEN COLUMN_COMMENT != '' THEN 1 ELSE 0 END) / COUNT(*)) * 100, 
            1
        ), 
        '%'
    ) AS '완성도'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME IN (
        SELECT TABLE_NAME 
        FROM INFORMATION_SCHEMA.TABLES 
        WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_TYPE = 'BASE TABLE'
    )
GROUP BY TABLE_NAME
ORDER BY TABLE_NAME;

-- -----------------------------------------------------------------------------
-- 6. 외래키 관계 및 참조 정보 조회
-- -----------------------------------------------------------------------------
SELECT 
    kcu.TABLE_NAME AS '테이블명',
    kcu.COLUMN_NAME AS '컬럼명',
    kcu.REFERENCED_TABLE_NAME AS '참조테이블',
    kcu.REFERENCED_COLUMN_NAME AS '참조컬럼',
    rc.UPDATE_RULE AS '업데이트규칙',
    rc.DELETE_RULE AS '삭제규칙'
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc
    ON kcu.CONSTRAINT_NAME = rc.CONSTRAINT_NAME
    AND kcu.TABLE_SCHEMA = rc.CONSTRAINT_SCHEMA
WHERE kcu.TABLE_SCHEMA = DATABASE()
    AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY kcu.TABLE_NAME, kcu.COLUMN_NAME;

-- -----------------------------------------------------------------------------
-- 7. 인덱스 정보 조회
-- -----------------------------------------------------------------------------
SELECT 
    TABLE_NAME AS '테이블명',
    INDEX_NAME AS '인덱스명',
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS '컬럼목록',
    CASE NON_UNIQUE 
        WHEN 0 THEN 'UNIQUE' 
        ELSE 'NON-UNIQUE' 
    END AS '유니크여부',
    INDEX_TYPE AS '인덱스타입'
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = DATABASE()
GROUP BY TABLE_NAME, INDEX_NAME, NON_UNIQUE, INDEX_TYPE
ORDER BY TABLE_NAME, INDEX_NAME;

-- -----------------------------------------------------------------------------
-- 8. 테이블 ERD 정보 추출용 쿼리
-- -----------------------------------------------------------------------------
SELECT 
    c.TABLE_NAME AS '테이블명',
    c.COLUMN_NAME AS '컬럼명',
    c.COLUMN_TYPE AS '데이터타입',
    c.IS_NULLABLE AS 'NULL허용',
    CASE 
        WHEN c.COLUMN_KEY = 'PRI' THEN 'PK'
        WHEN c.COLUMN_KEY = 'MUL' THEN 'FK'
        WHEN c.COLUMN_KEY = 'UNI' THEN 'UK'
        ELSE ''
    END AS '키타입',
    c.COLUMN_DEFAULT AS '기본값',
    c.COLUMN_COMMENT AS '설명',
    kcu.REFERENCED_TABLE_NAME AS '참조테이블',
    kcu.REFERENCED_COLUMN_NAME AS '참조컬럼'
FROM INFORMATION_SCHEMA.COLUMNS c
LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
    ON c.TABLE_SCHEMA = kcu.TABLE_SCHEMA
    AND c.TABLE_NAME = kcu.TABLE_NAME
    AND c.COLUMN_NAME = kcu.COLUMN_NAME
    AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
WHERE c.TABLE_SCHEMA = DATABASE()
    AND c.TABLE_NAME IN (
        'TB_CONSULTATION', 'TB_CASE', 'TB_BOARD', 
        'TB_FILE_RELATION', 'TB_FILE', 'TB_MANAGER',
        'TB_CMMN_CODE', 'TB_REVIEW', 'TB_CATEGORY', 'TB_PRODUCT'
    )
ORDER BY 
    c.TABLE_NAME, 
    c.ORDINAL_POSITION;

-- -----------------------------------------------------------------------------
-- 9. 데이터 딕셔너리 생성용 쿼리
-- -----------------------------------------------------------------------------
SELECT 
    CONCAT('### ', t.TABLE_NAME, ' (', t.TABLE_COMMENT, ')') AS 'Markdown_Header',
    NULL AS '컬럼명',
    NULL AS '타입',
    NULL AS '필수',
    NULL AS '설명'
FROM INFORMATION_SCHEMA.TABLES t
WHERE t.TABLE_SCHEMA = DATABASE()
    AND t.TABLE_TYPE = 'BASE TABLE'

UNION ALL

SELECT 
    CONCAT('| ', c.COLUMN_NAME, ' | ') AS 'Markdown_Row',
    c.COLUMN_TYPE AS '타입',
    CASE c.IS_NULLABLE 
        WHEN 'NO' THEN '필수' 
        ELSE '선택' 
    END AS '필수',
    CONCAT(
        c.COLUMN_COMMENT,
        CASE 
            WHEN c.COLUMN_KEY = 'PRI' THEN ' [PK]'
            WHEN kcu.REFERENCED_TABLE_NAME IS NOT NULL 
                THEN CONCAT(' [FK→', kcu.REFERENCED_TABLE_NAME, '.', kcu.REFERENCED_COLUMN_NAME, ']')
            ELSE ''
        END,
        ' |'
    ) AS '설명'
FROM INFORMATION_SCHEMA.COLUMNS c
LEFT JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
    ON c.TABLE_SCHEMA = kcu.TABLE_SCHEMA
    AND c.TABLE_NAME = kcu.TABLE_NAME
    AND c.COLUMN_NAME = kcu.COLUMN_NAME
    AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
WHERE c.TABLE_SCHEMA = DATABASE()
ORDER BY 
    c.TABLE_NAME, 
    c.ORDINAL_POSITION;

-- -----------------------------------------------------------------------------
-- 10. 코멘트 백업용 SELECT 문 생성
-- -----------------------------------------------------------------------------
SELECT CONCAT(
    'ALTER TABLE ', 
    TABLE_NAME, 
    ' COMMENT ''', 
    REPLACE(TABLE_COMMENT, '''', ''''''), 
    ''';'
) AS 'Backup_Script'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_TYPE = 'BASE TABLE'
    AND TABLE_COMMENT != ''

UNION ALL

SELECT CONCAT(
    'ALTER TABLE ', 
    TABLE_NAME, 
    ' MODIFY COLUMN ', 
    COLUMN_NAME, ' ',
    COLUMN_TYPE, ' ',
    CASE IS_NULLABLE WHEN 'NO' THEN 'NOT NULL' ELSE 'NULL' END,
    CASE 
        WHEN COLUMN_DEFAULT IS NOT NULL 
        THEN CONCAT(' DEFAULT ', 
            CASE 
                WHEN COLUMN_DEFAULT = 'CURRENT_TIMESTAMP' THEN COLUMN_DEFAULT
                ELSE CONCAT('''', COLUMN_DEFAULT, '''')
            END
        )
        ELSE ''
    END,
    ' COMMENT ''', 
    REPLACE(COLUMN_COMMENT, '''', ''''''), 
    ''';'
) AS 'Backup_Script'
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
    AND COLUMN_COMMENT != ''
ORDER BY 1;

-- =============================================================================
-- 사용 방법
-- =============================================================================
-- 1. 각 섹션의 쿼리를 개별적으로 실행하여 필요한 정보를 확인
-- 2. 결과를 Excel이나 문서로 내보내기 가능
-- 3. 정기적으로 실행하여 코멘트 관리 상태 모니터링
-- =============================================================================