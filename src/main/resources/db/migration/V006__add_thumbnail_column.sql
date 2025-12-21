-- =============================================================================
-- V006: 상품 테이블에 대표이미지(THUMBNAIL_SEQ) 컬럼 추가
-- =============================================================================

-- 대표이미지 컬럼 추가
ALTER TABLE TB_PRODUCT
ADD COLUMN THUMBNAIL_SEQ INT NULL COMMENT '대표이미지 파일번호 (TB_FILE 참조)' AFTER PRODUCT_DESC;

-- 인덱스 추가 (옵션)
CREATE INDEX IDX_PRODUCT_THUMBNAIL ON TB_PRODUCT (THUMBNAIL_SEQ);
