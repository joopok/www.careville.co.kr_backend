-- =============================================================================
-- V004__add_popular_yn_column.sql
-- TB_PRODUCT 테이블에 인기상품 여부(POPULAR_YN) 컬럼 추가
-- =============================================================================

-- POPULAR_YN 컬럼 추가 (인기 상품 배지 표시용)
ALTER TABLE `TB_PRODUCT`
ADD COLUMN `POPULAR_YN` CHAR(1) DEFAULT 'N' COMMENT '인기상품여부 (Y/N)'
AFTER `SALE_YN`;

-- 인덱스 추가 (인기 상품 필터링 최적화)
CREATE INDEX `IDX_PRODUCT_POPULAR` ON `TB_PRODUCT` (`POPULAR_YN`, `DEL_YN`);

-- 기존 데이터 초기화 (모두 'N'으로 설정)
UPDATE `TB_PRODUCT` SET `POPULAR_YN` = 'N' WHERE `POPULAR_YN` IS NULL;

-- =============================================================================
-- 사용 예시:
-- 인기 상품 조회: SELECT * FROM TB_PRODUCT WHERE POPULAR_YN = 'Y' AND DEL_YN = 'N';
-- 인기 상품 설정: UPDATE TB_PRODUCT SET POPULAR_YN = 'Y' WHERE PRODUCT_NO = 1;
-- =============================================================================
