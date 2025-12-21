-- FAQ 테이블 생성
CREATE TABLE IF NOT EXISTS TB_FAQ (
    FAQ_SEQ INT AUTO_INCREMENT PRIMARY KEY COMMENT 'FAQ 고유번호',
    CATEGORY VARCHAR(50) NULL COMMENT '카테고리',
    QUESTION VARCHAR(500) NOT NULL COMMENT '질문',
    ANSWER LONGTEXT NOT NULL COMMENT '답변',
    DISPLAY_YN VARCHAR(1) DEFAULT 'Y' COMMENT '노출여부 (Y/N)',
    DISPLAY_ORDER INT DEFAULT 999 COMMENT '노출순서',
    DEL_YN VARCHAR(1) DEFAULT 'N' COMMENT '삭제여부 (Y/N)',
    REG_DT DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    MOD_DT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ 관리 테이블';

-- FAQ 인덱스 생성
CREATE INDEX IF NOT EXISTS IDX_FAQ_DISPLAY ON TB_FAQ(DISPLAY_YN, DEL_YN);
CREATE INDEX IF NOT EXISTS IDX_FAQ_ORDER ON TB_FAQ(DISPLAY_ORDER, FAQ_SEQ);

-- 초기 FAQ 데이터 삽입
INSERT INTO TB_FAQ (CATEGORY, QUESTION, ANSWER, DISPLAY_YN, DISPLAY_ORDER) VALUES
('예약', '예약은 어떻게 하나요?', '전화(1600-9762) 또는 온라인 문의를 통해 예약 가능합니다.', 'Y', 1),
('비용', '견적은 무료인가요?', '네, 방문 견적은 완전 무료입니다.', 'Y', 2),
('서비스', '작업 시간은 얼마나 걸리나요?', '평균 30평 기준 4-6시간 정도 소요됩니다.', 'Y', 3),
('비용', '결제는 어떻게 진행되나요?', '서비스 완료 후 현장 결제 또는 계좌 이체를 통해 진행됩니다. 카드 결제도 가능합니다.', 'Y', 4),
('서비스', '서비스 전에 준비할 것이 있나요?', '귀중품이나 파손되기 쉬운 물품은 미리 안전한 곳으로 옮겨주시면 더욱 원활한 서비스가 가능합니다.', 'Y', 5);
