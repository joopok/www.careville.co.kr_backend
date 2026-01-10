-- 히어로 섹션 배경 이미지 설정 추가 (최대 10개)
-- CONFIG_TYPE: FILE 타입 추가

INSERT INTO TB_CONFIG (CONFIG_KEY, CONFIG_VALUE, CONFIG_DESC, CONFIG_GROUP, CONFIG_TYPE, DISPLAY_ORDER) VALUES
('HERO_IMG_01', '', '히어로 이미지 1', 'HERO', 'FILE', 1),
('HERO_IMG_02', '', '히어로 이미지 2', 'HERO', 'FILE', 2),
('HERO_IMG_03', '', '히어로 이미지 3', 'HERO', 'FILE', 3),
('HERO_IMG_04', '', '히어로 이미지 4', 'HERO', 'FILE', 4),
('HERO_IMG_05', '', '히어로 이미지 5', 'HERO', 'FILE', 5),
('HERO_IMG_06', '', '히어로 이미지 6', 'HERO', 'FILE', 6),
('HERO_IMG_07', '', '히어로 이미지 7', 'HERO', 'FILE', 7),
('HERO_IMG_08', '', '히어로 이미지 8', 'HERO', 'FILE', 8),
('HERO_IMG_09', '', '히어로 이미지 9', 'HERO', 'FILE', 9),
('HERO_IMG_10', '', '히어로 이미지 10', 'HERO', 'FILE', 10)
ON DUPLICATE KEY UPDATE CONFIG_KEY=CONFIG_KEY;
