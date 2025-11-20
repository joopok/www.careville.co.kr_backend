-- =============================================================================
-- MariaDB 10.1.x 최적화 스키마
-- =============================================================================
-- Database: careville
-- Engine: InnoDB
-- Charset: utf8mb4 (UTF-8 with emoji support)
-- Collation: utf8mb4_unicode_ci
-- Version: MariaDB 10.1.x Compatible
-- Date: 2025-09-04
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 데이터베이스 설정
-- -----------------------------------------------------------------------------
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_DATABASE = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_SERVER = utf8mb4;
SET COLLATION_CONNECTION = utf8mb4_unicode_ci;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
SET FOREIGN_KEY_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- -----------------------------------------------------------------------------
-- 데이터베이스 생성 (필요시)
-- -----------------------------------------------------------------------------
-- CREATE DATABASE IF NOT EXISTS `careville` 
-- DEFAULT CHARACTER SET utf8mb4 
-- DEFAULT COLLATE utf8mb4_unicode_ci;
-- USE `careville`;

-- -----------------------------------------------------------------------------
-- 기존 테이블 삭제 (초기화용)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS `TB_FILE_RELATION`;
DROP TABLE IF EXISTS `TB_REVIEW`;
DROP TABLE IF EXISTS `TB_CONSULTATION`;
DROP TABLE IF EXISTS `TB_CASE`;
DROP TABLE IF EXISTS `TB_BOARD`;
DROP TABLE IF EXISTS `TB_FILE`;
DROP TABLE IF EXISTS `TB_MANAGER`;
DROP TABLE IF EXISTS `TB_CMMN_CODE`;
DROP TABLE IF EXISTS `TB_PRODUCT`;
DROP TABLE IF EXISTS `TB_CATEGORY`;

-- -----------------------------------------------------------------------------
-- 1. TB_CATEGORY (서비스 카테고리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_CATEGORY` (
    `SERVICE_CD`     VARCHAR(3) NOT NULL COMMENT '서비스코드 (Primary Key)',
    `SERVICE_NM`     VARCHAR(100) NOT NULL COMMENT '서비스명',
    `SERVICE_ORDER`  INT(11) DEFAULT 0 COMMENT '노출 정렬순서',
    `USE_YN`         CHAR(1) DEFAULT 'Y' COMMENT '사용여부 (Y/N)',
    `DEL_YN`         CHAR(1) DEFAULT 'N' COMMENT '삭제여부 (Y/N, 논리삭제)',
    `REG_USER_ID`    VARCHAR(50) DEFAULT NULL COMMENT '등록자 ID',
    `REG_DT`         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    `MOD_USER_ID`    VARCHAR(50) DEFAULT NULL COMMENT '수정자 ID',
    `MOD_DT`         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종수정일시',
    PRIMARY KEY (`SERVICE_CD`),
    KEY `IDX_CATEGORY_USE` (`USE_YN`, `DEL_YN`),
    KEY `IDX_CATEGORY_ORDER` (`SERVICE_ORDER`, `SERVICE_CD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='서비스 카테고리 정보 관리 테이블';

-- -----------------------------------------------------------------------------
-- 2. TB_PRODUCT (상품 정보 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_PRODUCT` (
    `PRODUCT_NO`       INT(11) NOT NULL AUTO_INCREMENT COMMENT '상품번호',
    `SERVICE_CD`       VARCHAR(3) NOT NULL COMMENT '서비스코드 (TB_CATEGORY 참조)',
    `PRODUCT_NM`       VARCHAR(200) NOT NULL COMMENT '상품명',
    `PRODUCT_DESC`     LONGTEXT DEFAULT NULL COMMENT '상품 상세설명',
    `AREA_TYPE`        VARCHAR(50) DEFAULT NULL COMMENT '적용평형 (예: 20-30평형)',
    `ORIGINAL_PRICE`   INT(11) DEFAULT 0 COMMENT '정가',
    `SALE_PRICE`       INT(11) DEFAULT 0 COMMENT '판매가',
    `DISCOUNT_RATE`    INT(11) DEFAULT 0 COMMENT '할인율 (%)',
    `SERVICE_TIME`     VARCHAR(50) DEFAULT NULL COMMENT '서비스 소요시간',
    `SERVICE_INCLUDES` LONGTEXT DEFAULT NULL COMMENT '서비스 포함사항 (JSON)',
    `DISPLAY_ORDER`    INT(11) DEFAULT 999 COMMENT '노출순서',
    `DISPLAY_YN`       CHAR(1) DEFAULT 'Y' COMMENT '노출여부 (Y/N)',
    `SALE_YN`          CHAR(1) DEFAULT 'Y' COMMENT '판매여부 (Y/N)',
    `DEL_YN`           CHAR(1) DEFAULT 'N' COMMENT '삭제여부 (Y/N, 논리삭제)',
    `REG_USER_ID`      VARCHAR(50) DEFAULT NULL COMMENT '등록자 ID',
    `REG_DT`           DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    `MOD_USER_ID`      VARCHAR(50) DEFAULT NULL COMMENT '수정자 ID',
    `MOD_DT`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종수정일시',
    PRIMARY KEY (`PRODUCT_NO`),
    KEY `IDX_PRODUCT_SERVICE` (`SERVICE_CD`),
    KEY `IDX_PRODUCT_DISPLAY` (`DISPLAY_YN`, `DEL_YN`),
    KEY `IDX_PRODUCT_ORDER` (`DISPLAY_ORDER`, `PRODUCT_NO`),
    KEY `IDX_PRODUCT_SALE` (`SALE_YN`, `DEL_YN`),
    CONSTRAINT `FK_PRODUCT_CATEGORY` FOREIGN KEY (`SERVICE_CD`) 
        REFERENCES `TB_CATEGORY` (`SERVICE_CD`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 정보 관리 테이블';

-- -----------------------------------------------------------------------------
-- 3. TB_CONSULTATION (상담/예약 관리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_CONSULTATION` (
    `CNSLT_SEQ`    INT(11) NOT NULL AUTO_INCREMENT COMMENT '상담/예약 일련번호',
    `NM`           VARCHAR(30) NOT NULL COMMENT '고객 이름',
    `TEL1`         VARCHAR(20) DEFAULT NULL COMMENT '전화번호 (일반)',
    `TEL2`         VARCHAR(20) NOT NULL COMMENT '휴대폰번호 (필수)',
    `ZIP`          VARCHAR(5) DEFAULT NULL COMMENT '우편번호',
    `ADRES1`       VARCHAR(99) DEFAULT NULL COMMENT '기본주소',
    `ADRES2`       VARCHAR(99) DEFAULT NULL COMMENT '상세주소',
    `SERVICE_CD`   VARCHAR(3) DEFAULT NULL COMMENT '서비스코드 (TB_CATEGORY 참조, 상담)',
    `PRODUCT_NO`   INT(11) DEFAULT NULL COMMENT '상품번호 (TB_PRODUCT 참조, 예약)',
    `HOPE_DAY`     VARCHAR(8) DEFAULT NULL COMMENT '희망일자 (YYYYMMDD)',
    `INQRY_CN`     LONGTEXT NOT NULL COMMENT '문의 내용',
    `REG_DT`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '문의 등록일시',
    `PW`           VARCHAR(99) DEFAULT NULL COMMENT '비밀번호 (조회용)',
    `ANSWER`       LONGTEXT DEFAULT NULL COMMENT '문의 답변 내용',
    `ANSWER_DT`    DATETIME DEFAULT NULL COMMENT '답변 등록일시',
    `ANSWER_ID`    VARCHAR(30) DEFAULT NULL COMMENT '답변 작성자 ID',
    `STTUS_CD`     VARCHAR(3) DEFAULT NULL COMMENT '진행상태 코드',
    `REQ_TYPE`     VARCHAR(3) NOT NULL COMMENT '요청구분 (001:상담, 002:예약)',
    PRIMARY KEY (`CNSLT_SEQ`),
    KEY `IDX_CONSULTATION_DATE` (`REG_DT`),
    KEY `IDX_CONSULTATION_STATUS` (`STTUS_CD`),
    KEY `IDX_CONSULTATION_TYPE` (`REQ_TYPE`),
    KEY `IDX_CONSULTATION_SERVICE` (`SERVICE_CD`),
    KEY `IDX_CONSULTATION_PRODUCT` (`PRODUCT_NO`),
    CONSTRAINT `FK_CONSULTATION_CATEGORY` FOREIGN KEY (`SERVICE_CD`) 
        REFERENCES `TB_CATEGORY` (`SERVICE_CD`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_CONSULTATION_PRODUCT` FOREIGN KEY (`PRODUCT_NO`) 
        REFERENCES `TB_PRODUCT` (`PRODUCT_NO`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상담 및 예약 관리 테이블';

-- -----------------------------------------------------------------------------
-- 4. TB_CASE (사례 관리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_CASE` (
    `CASE_SEQ`  INT(11) NOT NULL AUTO_INCREMENT COMMENT '사례 일련번호',
    `CASE_SJ`   VARCHAR(100) NOT NULL COMMENT '사례 제목',
    `CASE_CN`   LONGTEXT DEFAULT NULL COMMENT '사례 내용',
    `REG_NM`    VARCHAR(50) NOT NULL COMMENT '작성자 ID',
    `FILE_SEQ`  INT(11) NOT NULL COMMENT '대표이미지 파일번호 (TB_FILE 참조)',
    `HASHTAG`   VARCHAR(500) DEFAULT NULL COMMENT '해시태그 (쉼표 구분)',
    `RGS_DT`    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`CASE_SEQ`),
    KEY `IDX_CASE_DATE` (`RGS_DT`),
    KEY `IDX_CASE_FILE` (`FILE_SEQ`),
    FULLTEXT KEY `FT_CASE_HASHTAG` (`HASHTAG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='서비스 사례 관리 테이블';

-- -----------------------------------------------------------------------------
-- 5. TB_BOARD (게시판 관리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_BOARD` (
    `BOARD_SEQ`  INT(11) NOT NULL AUTO_INCREMENT COMMENT '게시글 일련번호',
    `BOARD_GB`   VARCHAR(10) NOT NULL COMMENT '게시판 구분 (공지, FAQ, 포토 등)',
    `NOTICE_YN`  CHAR(1) NOT NULL DEFAULT 'N' COMMENT '공지사항 여부 (Y/N)',
    `BOARD_SJ`   VARCHAR(100) NOT NULL COMMENT '게시글 제목',
    `BOARD_CN`   LONGTEXT DEFAULT NULL COMMENT '게시글 내용',
    `REG_NM`     VARCHAR(100) NOT NULL COMMENT '작성자명',
    `RGS_DT`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    `SECRET_YN`  CHAR(1) DEFAULT 'N' COMMENT '비밀글 여부 (Y/N)',
    `SECRET_PW`  VARCHAR(1000) DEFAULT NULL COMMENT '비밀글 비밀번호 (암호화)',
    PRIMARY KEY (`BOARD_SEQ`),
    KEY `IDX_BOARD_TYPE` (`BOARD_GB`),
    KEY `IDX_BOARD_NOTICE` (`NOTICE_YN`, `RGS_DT`),
    KEY `IDX_BOARD_DATE` (`RGS_DT`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시판 관리 테이블';

-- -----------------------------------------------------------------------------
-- 6. TB_FILE (파일 정보 관리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_FILE` (
    `FILE_SEQ`     INT(11) NOT NULL AUTO_INCREMENT COMMENT '파일 일련번호',
    `FILE_REAL_NM` VARCHAR(500) NOT NULL COMMENT '원본 파일명',
    `FILE_FAKE_NM` VARCHAR(500) NOT NULL COMMENT '저장 파일명 (시스템생성)',
    `FILE_PATH`    VARCHAR(100) NOT NULL COMMENT '파일 저장경로',
    `FILE_EXTSN`   VARCHAR(10) NOT NULL COMMENT '파일 확장자',
    `FILE_SIZE`    INT(11) NOT NULL COMMENT '파일 크기 (bytes)',
    `RGS_DT`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`FILE_SEQ`),
    KEY `IDX_FILE_DATE` (`RGS_DT`),
    KEY `IDX_FILE_FAKE` (`FILE_FAKE_NM`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='파일 정보 관리 테이블';

-- -----------------------------------------------------------------------------
-- 7. TB_FILE_RELATION (파일 관계 관리 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_FILE_RELATION` (
    `FILE_RELATE_SEQ` INT(11) NOT NULL AUTO_INCREMENT COMMENT '파일관계 일련번호',
    `FILE_TRGET_SE`   VARCHAR(20) NOT NULL COMMENT '대상 구분 (CNSLT:상담, BOARD:게시판 등)',
    `FILE_TRGET_SEQ`  INT(11) NOT NULL COMMENT '대상 테이블 고유번호',
    `FILE_SEQ`        INT(11) NOT NULL COMMENT '파일 일련번호 (TB_FILE 참조)',
    `RGS_DT`          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`FILE_RELATE_SEQ`),
    KEY `IDX_FILE_REL_TARGET` (`FILE_TRGET_SE`, `FILE_TRGET_SEQ`),
    KEY `IDX_FILE_REL_FILE` (`FILE_SEQ`),
    CONSTRAINT `FK_FILE_RELATION_FILE` FOREIGN KEY (`FILE_SEQ`) 
        REFERENCES `TB_FILE` (`FILE_SEQ`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='파일 연관관계 관리 테이블';

-- -----------------------------------------------------------------------------
-- 8. TB_MANAGER (관리자 정보 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_MANAGER` (
    `MNGR_SEQ`   INT(11) NOT NULL AUTO_INCREMENT COMMENT '관리자 일련번호',
    `MNGR_ID`    VARCHAR(50) NOT NULL COMMENT '관리자 로그인 ID',
    `MNGR_PW`    VARCHAR(99) NOT NULL COMMENT '관리자 비밀번호 (암호화)',
    `MNGR_NM`    VARCHAR(20) NOT NULL COMMENT '관리자 성명',
    `MNGR_NCNM`  VARCHAR(20) NOT NULL COMMENT '관리자 닉네임/별칭',
    `MNGR_TEL`   VARCHAR(20) DEFAULT NULL COMMENT '관리자 연락처',
    `MNGR_STTUS` CHAR(1) NOT NULL DEFAULT 'A' COMMENT '계정 상태 (A:활성, I:비활성, L:잠금)',
    `RGS_DT`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`MNGR_SEQ`),
    UNIQUE KEY `UK_MANAGER_ID` (`MNGR_ID`),
    KEY `IDX_MANAGER_STATUS` (`MNGR_STTUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시스템 관리자 정보 테이블';

-- -----------------------------------------------------------------------------
-- 9. TB_CMMN_CODE (공통 코드 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_CMMN_CODE` (
    `GROUP_CD`    CHAR(3) NOT NULL COMMENT '그룹코드 (대분류)',
    `GROUP_NM`    VARCHAR(99) NOT NULL COMMENT '그룹코드명',
    `CODE`        CHAR(3) NOT NULL COMMENT '상세코드 (소분류)',
    `CODE_NM`     VARCHAR(99) NOT NULL COMMENT '상세코드명',
    `CODE_USE_YN` CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '코드 사용여부 (Y/N)',
    `SORT_ORDER`  INT(11) DEFAULT 0 COMMENT '정렬순서',
    PRIMARY KEY (`GROUP_CD`, `CODE`),
    KEY `IDX_CMMN_CODE_USE` (`CODE_USE_YN`),
    KEY `IDX_CMMN_CODE_ORDER` (`GROUP_CD`, `SORT_ORDER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시스템 공통코드 관리 테이블';

-- -----------------------------------------------------------------------------
-- 10. TB_REVIEW (고객 후기 테이블)
-- -----------------------------------------------------------------------------
CREATE TABLE `TB_REVIEW` (
    `REVIEW_SEQ` INT(11) NOT NULL AUTO_INCREMENT COMMENT '후기 일련번호',
    `REVIEW_NM`  VARCHAR(50) NOT NULL COMMENT '작성자명',
    `REVIEW_CN`  LONGTEXT NOT NULL COMMENT '후기 내용',
    `STAR_RATE`  INT(11) NOT NULL COMMENT '평점 (1~5점)',
    `SERVICE_CD` VARCHAR(3) DEFAULT NULL COMMENT '서비스코드 (TB_CATEGORY 참조)',
    `PRODUCT_NO` INT(11) DEFAULT NULL COMMENT '상품번호 (TB_PRODUCT 참조)',
    `SVC_DATE`   VARCHAR(8) DEFAULT NULL COMMENT '서비스 이용일자 (YYYYMMDD)',
    `DISP_YN`    CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '노출여부 (Y/N)',
    `PW`         VARCHAR(99) DEFAULT NULL COMMENT '비밀번호 (수정/삭제용)',
    `RGS_DT`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    PRIMARY KEY (`REVIEW_SEQ`),
    KEY `IDX_REVIEW_SERVICE` (`SERVICE_CD`),
    KEY `IDX_REVIEW_PRODUCT` (`PRODUCT_NO`),
    KEY `IDX_REVIEW_DISPLAY` (`DISP_YN`, `RGS_DT`),
    KEY `IDX_REVIEW_STAR` (`STAR_RATE`),
    CONSTRAINT `FK_REVIEW_CATEGORY` FOREIGN KEY (`SERVICE_CD`) 
        REFERENCES `TB_CATEGORY` (`SERVICE_CD`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `FK_REVIEW_PRODUCT` FOREIGN KEY (`PRODUCT_NO`) 
        REFERENCES `TB_PRODUCT` (`PRODUCT_NO`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `CHK_REVIEW_STAR` CHECK (`STAR_RATE` >= 1 AND `STAR_RATE` <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고객 서비스 후기 관리 테이블';

-- -----------------------------------------------------------------------------
-- 뷰 생성 (옵션)
-- -----------------------------------------------------------------------------
-- 활성 상담 조회 뷰
CREATE OR REPLACE VIEW `V_ACTIVE_CONSULTATIONS` AS
SELECT 
    c.*,
    cat.SERVICE_NM,
    p.PRODUCT_NM
FROM TB_CONSULTATION c
LEFT JOIN TB_CATEGORY cat ON c.SERVICE_CD = cat.SERVICE_CD
LEFT JOIN TB_PRODUCT p ON c.PRODUCT_NO = p.PRODUCT_NO
WHERE c.STTUS_CD = '001';

-- 노출 리뷰 조회 뷰
CREATE OR REPLACE VIEW `V_DISPLAY_REVIEWS` AS
SELECT 
    r.*,
    cat.SERVICE_NM,
    p.PRODUCT_NM
FROM TB_REVIEW r
LEFT JOIN TB_CATEGORY cat ON r.SERVICE_CD = cat.SERVICE_CD
LEFT JOIN TB_PRODUCT p ON r.PRODUCT_NO = p.PRODUCT_NO
WHERE r.DISP_YN = 'Y'
ORDER BY r.RGS_DT DESC;

-- -----------------------------------------------------------------------------
-- 저장 프로시저 예시 (옵션)
-- -----------------------------------------------------------------------------
DELIMITER $$

-- 리뷰 평점 통계 프로시저
CREATE PROCEDURE `SP_GET_REVIEW_STATS`(
    IN p_service_cd VARCHAR(3)
)
BEGIN
    SELECT 
        COUNT(*) AS total_reviews,
        AVG(STAR_RATE) AS avg_rating,
        MAX(STAR_RATE) AS max_rating,
        MIN(STAR_RATE) AS min_rating,
        SUM(CASE WHEN STAR_RATE = 5 THEN 1 ELSE 0 END) AS five_star_count,
        SUM(CASE WHEN STAR_RATE = 4 THEN 1 ELSE 0 END) AS four_star_count,
        SUM(CASE WHEN STAR_RATE = 3 THEN 1 ELSE 0 END) AS three_star_count,
        SUM(CASE WHEN STAR_RATE = 2 THEN 1 ELSE 0 END) AS two_star_count,
        SUM(CASE WHEN STAR_RATE = 1 THEN 1 ELSE 0 END) AS one_star_count
    FROM TB_REVIEW
    WHERE SERVICE_CD = p_service_cd
        AND DISP_YN = 'Y';
END$$

DELIMITER ;

-- -----------------------------------------------------------------------------
-- 파티셔닝 예시 (대용량 데이터 처리용 - 옵션)
-- -----------------------------------------------------------------------------
-- TB_CONSULTATION 테이블 월별 파티셔닝 (MariaDB 10.1에서 지원)
/*
ALTER TABLE TB_CONSULTATION
PARTITION BY RANGE (YEAR(REG_DT) * 100 + MONTH(REG_DT)) (
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p202505 VALUES LESS THAN (202506),
    PARTITION p202506 VALUES LESS THAN (202507),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/

-- -----------------------------------------------------------------------------
-- 실행 후 설정
-- -----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

-- -----------------------------------------------------------------------------
-- 권한 설정 예시
-- -----------------------------------------------------------------------------
-- GRANT SELECT, INSERT, UPDATE, DELETE ON careville.* TO 'app_user'@'%';
-- GRANT EXECUTE ON careville.* TO 'app_user'@'%';
-- FLUSH PRIVILEGES;

-- =============================================================================
-- 최적화 완료 메시지
-- =============================================================================
-- MariaDB 10.1.x 호환 스키마가 성공적으로 생성되었습니다.
-- - 모든 테이블: InnoDB 엔진 사용
-- - 문자셋: utf8mb4 (이모지 지원)
-- - 외래키 제약조건 적용
-- - 인덱스 최적화 완료
-- - 체크 제약조건 추가 (MariaDB 10.2+ 호환)
-- =============================================================================