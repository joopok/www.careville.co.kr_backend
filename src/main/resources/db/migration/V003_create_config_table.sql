-- TB_CONFIG 테이블 생성
CREATE TABLE IF NOT EXISTS TB_CONFIG (
    CONFIG_KEY VARCHAR(50) PRIMARY KEY COMMENT '설정 키',
    CONFIG_VALUE TEXT COMMENT '설정 값',
    CONFIG_DESC VARCHAR(200) COMMENT '설정 설명',
    CONFIG_GROUP VARCHAR(50) DEFAULT 'BASIC' COMMENT '설정 그룹 (BASIC, CONTACT, BUSINESS, SNS)',
    CONFIG_TYPE VARCHAR(20) DEFAULT 'TEXT' COMMENT '입력 타입 (TEXT, TEXTAREA, EMAIL, TEL, URL, NUMBER)',
    DISPLAY_ORDER INT DEFAULT 999 COMMENT '표시 순서',
    REG_DT DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    MOD_DT DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='시스템 설정 테이블';

-- 설정 초기 데이터 (siteConfig 기반)
INSERT INTO TB_CONFIG (CONFIG_KEY, CONFIG_VALUE, CONFIG_DESC, CONFIG_GROUP, CONFIG_TYPE, DISPLAY_ORDER) VALUES
-- 기본 정보 (BASIC)
('SITE_NAME', '케어빌 - 프리미엄 청소 서비스', '사이트명', 'BASIC', 'TEXT', 1),
('COMPANY_NAME', '주식회사 케이빌', '회사명', 'BASIC', 'TEXT', 2),
('CEO_NAME', '이경숙', '대표자명', 'BASIC', 'TEXT', 3),
('BIZ_NUMBER', '276-87-03677', '사업자등록번호', 'BASIC', 'TEXT', 4),
('BIZ_TYPE', '서비스, 건설업, 도소매', '업종', 'BASIC', 'TEXT', 5),
('SITE_URL', 'https://www.careville.co.kr', '사이트 URL', 'BASIC', 'URL', 6),
('META_DESCRIPTION', '전문적이고 신뢰할 수 있는 청소 서비스를 제공합니다.', 'SEO 설명', 'BASIC', 'TEXTAREA', 7),

-- 연락처 정보 (CONTACT)
('PHONE', '1600-9762', '대표 전화번호', 'CONTACT', 'TEL', 10),
('EMAIL', 'seung0910@naver.com', '대표 이메일', 'CONTACT', 'EMAIL', 11),
('ADDRESS_HQ', '경기 고양시 일산동구 정발산로 31-10, 806호(장항동, 파크프라자)', '본사 주소', 'CONTACT', 'TEXTAREA', 12),
('ADDRESS_BRANCH', '경기 고양시 으뜸로8, 504호(덕은아이에스비즈타워센트럴 1차)', '지점 주소', 'CONTACT', 'TEXTAREA', 13),

-- 운영 정보 (BUSINESS)
('HOURS_WEEKDAY', '09:00 - 18:00', '평일 영업시간', 'BUSINESS', 'TEXT', 20),
('HOURS_WEEKEND', '09:00 - 15:00', '주말 영업시간', 'BUSINESS', 'TEXT', 21),
('HOURS_EMERGENCY', '24시간 상담 가능', '긴급 상담', 'BUSINESS', 'TEXT', 22),

-- SNS 정보 (SNS)
('NAVER_BLOG', 'https://blog.naver.com/on_totalcare', '네이버 블로그', 'SNS', 'URL', 30),
('INSTAGRAM', '', '인스타그램', 'SNS', 'URL', 31),
('YOUTUBE', '', '유튜브', 'SNS', 'URL', 32),
('FACEBOOK', '', '페이스북', 'SNS', 'URL', 33),
('KAKAO_CHANNEL', '', '카카오톡 채널', 'SNS', 'URL', 34)
ON DUPLICATE KEY UPDATE CONFIG_KEY=CONFIG_KEY;
