-- =============================================================================
-- MariaDB 10.1.x 최적화 초기 데이터
-- =============================================================================
-- Database: careville
-- Engine: InnoDB
-- Charset: utf8mb4 (UTF-8 with emoji support)
-- Version: MariaDB 10.1.x Compatible
-- Date: 2025-01-04
-- =============================================================================

-- -----------------------------------------------------------------------------
-- 데이터베이스 설정
-- -----------------------------------------------------------------------------
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;
SET CHARACTER_SET_SERVER = utf8mb4;
SET COLLATION_CONNECTION = utf8mb4_unicode_ci;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';
SET FOREIGN_KEY_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- 트랜잭션 시작
START TRANSACTION;

-- -----------------------------------------------------------------------------
-- 기존 데이터 삭제 (재실행 가능)
-- -----------------------------------------------------------------------------
DELETE FROM `TB_FILE_RELATION`;
DELETE FROM `TB_REVIEW`;
DELETE FROM `TB_CONSULTATION`;
DELETE FROM `TB_CASE`;
DELETE FROM `TB_BOARD`;
DELETE FROM `TB_FILE`;
DELETE FROM `TB_MANAGER`;
DELETE FROM `TB_CMMN_CODE`;
DELETE FROM `TB_PRODUCT`;
DELETE FROM `TB_CATEGORY`;

-- AUTO_INCREMENT 초기화
ALTER TABLE `TB_FILE_RELATION` AUTO_INCREMENT = 1;
ALTER TABLE `TB_REVIEW` AUTO_INCREMENT = 1;
ALTER TABLE `TB_CONSULTATION` AUTO_INCREMENT = 1;
ALTER TABLE `TB_CASE` AUTO_INCREMENT = 1;
ALTER TABLE `TB_BOARD` AUTO_INCREMENT = 1;
ALTER TABLE `TB_FILE` AUTO_INCREMENT = 1;
ALTER TABLE `TB_MANAGER` AUTO_INCREMENT = 1;
ALTER TABLE `TB_PRODUCT` AUTO_INCREMENT = 1;

-- -----------------------------------------------------------------------------
-- 1. TB_MANAGER - 관리자 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_MANAGER`
    (`MNGR_ID`, `MNGR_PW`, `MNGR_NM`, `MNGR_NCNM`, `MNGR_TEL`, `MNGR_STTUS`, `RGS_DT`)
VALUES
    ('superadmin', '$2a$10$slYQmyNdGzin7olVZeVaeuK1kHLiQuZ7q11UxGEgI7EL7DlH6cUFm', '이순신', '슈퍼 관리자', '0212341234', 'A', NOW()),
    ('admin', '$2a$10$slYQmyNdGzin7olVZeVaeuK1kHLiQuZ7q11UxGEgI7EL7DlH6cUFm', '홍길동', '일반 관리자', '0212345678', 'A', NOW()),
    ('manager', '$2a$10$slYQmyNdGzin7olVZeVaeuK1kHLiQuZ7q11UxGEgI7EL7DlH6cUFm', '김철수', '운영 매니저', '0219876543', 'A', NOW());

-- -----------------------------------------------------------------------------
-- 2. TB_CMMN_CODE - 공통 코드 데이터
-- -----------------------------------------------------------------------------
-- 상담/예약 진행상태 코드
INSERT INTO `TB_CMMN_CODE` 
    (`GROUP_CD`, `GROUP_NM`, `CODE`, `CODE_NM`, `CODE_USE_YN`, `SORT_ORDER`) 
VALUES
    ('002', '상담,예약진행상태', '001', '접수', 'Y', 1),
    ('002', '상담,예약진행상태', '002', '진행중', 'Y', 2),
    ('002', '상담,예약진행상태', '003', '완료', 'Y', 3),
    ('002', '상담,예약진행상태', '004', '취소', 'Y', 4),
    ('002', '상담,예약진행상태', '005', '보류', 'Y', 5);

-- 상담/예약 구분 코드
INSERT INTO `TB_CMMN_CODE` 
    (`GROUP_CD`, `GROUP_NM`, `CODE`, `CODE_NM`, `CODE_USE_YN`, `SORT_ORDER`) 
VALUES
    ('003', '상담,예약구분', '001', '상담', 'Y', 1),
    ('003', '상담,예약구분', '002', '예약', 'Y', 2);

-- 게시판 구분 코드
INSERT INTO `TB_CMMN_CODE` 
    (`GROUP_CD`, `GROUP_NM`, `CODE`, `CODE_NM`, `CODE_USE_YN`, `SORT_ORDER`) 
VALUES
    ('004', '게시판구분', '001', '공지사항', 'Y', 1),
    ('004', '게시판구분', '002', 'FAQ', 'Y', 2),
    ('004', '게시판구분', '003', '포토갤러리', 'Y', 3),
    ('004', '게시판구분', '004', '시공사례', 'Y', 4),
    ('004', '게시판구분', '005', '자유게시판', 'Y', 5);

-- -----------------------------------------------------------------------------
-- 3. TB_CATEGORY - 서비스 카테고리 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_CATEGORY` 
    (`SERVICE_CD`, `SERVICE_NM`, `SERVICE_ORDER`, `USE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`) 
VALUES 
    ('001', '에어컨 케어 및 세척', 1, 'Y', 'N', 'system', NOW()),
    ('002', '설치/교체 서비스', 2, 'Y', 'N', 'system', NOW()),
    ('003', '상가/사무실 시공', 3, 'Y', 'N', 'system', NOW()),
    ('004', '매트리스 청소(케어)', 4, 'Y', 'N', 'system', NOW()),
    ('005', '세탁기 케어', 5, 'Y', 'N', 'system', NOW()),
    ('006', '욕실 전문 시공', 6, 'Y', 'N', 'system', NOW()),
    ('007', '환풍기 설치', 7, 'Y', 'N', 'system', NOW()),
    ('008', '프리미엄 주방케어', 8, 'Y', 'N', 'system', NOW()),
    ('009', '특수청소', 9, 'Y', 'N', 'system', NOW()),
    ('010', '주방상판', 10, 'Y', 'N', 'system', NOW()),
    ('011', '층간소음매트', 11, 'Y', 'N', 'system', NOW());

-- -----------------------------------------------------------------------------
-- 4. TB_PRODUCT - 상품 데이터 (샘플 30개)
-- -----------------------------------------------------------------------------
-- 에어컨 케어 상품
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('001', '벽걸이 에어컨 기본청소', '벽걸이 에어컨 1대 분해청소 서비스', '1대', 50000, 40000, 20, '1시간', '["완전 분해청소","필터 세척","살균소독","냄새제거"]', 1, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('001', '벽걸이 에어컨 2대 패키지', '벽걸이 에어컨 2대 동시 청소', '2대', 100000, 75000, 25, '2시간', '["2대 동시청소","필터 교체","살균코팅","1년 보증"]', 2, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('001', '스탠드 에어컨 청소', '스탠드형 에어컨 전문 청소', '1대', 70000, 60000, 14, '1.5시간', '["완전분해","내부청소","필터교체","항균처리"]', 3, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('001', '시스템 에어컨 청소', '천장형 시스템 에어컨 청소', '1대', 80000, 65000, 19, '1.5시간', '["시스템전문청소","덕트청소","필터교체","성능점검"]', 4, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('001', '업소용 대형 에어컨', '업소용 대형 에어컨 청소', '1대', 150000, 120000, 20, '3시간', '["대형장비청소","압축기점검","냉매확인","정기관리"]', 5, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- 설치/교체 서비스
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('002', '에어컨 이전설치', '에어컨 이전 및 재설치 서비스', '1대', 100000, 80000, 20, '2시간', '["철거작업","이전설치","배관연결","시운전"]', 10, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('002', '에어컨 신규설치', '신규 에어컨 설치 서비스', '1대', 150000, 120000, 20, '3시간', '["신규설치","배관공사","전기공사","시운전"]', 11, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('002', '환풍기 교체설치', '욕실/주방 환풍기 교체', '1대', 80000, 65000, 19, '1시간', '["기존철거","신규설치","덕트연결","작동확인"]', 12, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('002', '세탁기 설치', '세탁기 설치 및 연결', '1대', 50000, 40000, 20, '1시간', '["설치작업","급배수연결","수평조절","시운전"]', 13, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('002', '정수기 설치', '정수기 설치 서비스', '1대', 40000, 35000, 13, '30분', '["설치작업","배관연결","필터설치","사용설명"]', 14, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- 상가/사무실 시공
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('003', '소규모 사무실 청소', '50평 이하 사무실 청소', '50평 이하', 200000, 160000, 20, '4시간', '["전체청소","카펫청소","유리창청소","화장실청소"]', 20, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('003', '중규모 사무실 청소', '100평 사무실 정기청소', '50-100평', 350000, 280000, 20, '6시간', '["전구역청소","바닥왁싱","소독작업","폐기물처리"]', 21, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('003', '대규모 사무실 청소', '200평 이상 대형 사무실', '200평 이상', 600000, 500000, 17, '8시간', '["층별청소","특수구역","심야작업","정기계약"]', 22, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('003', '상가 입주청소', '신규 상가 입주청소', '100평', 400000, 320000, 20, '6시간', '["입주청소","바닥광택","유리창","간판청소"]', 23, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('003', '병원 특수청소', '의료시설 전문청소', '150평', 500000, 420000, 16, '6시간', '["의료시설청소","소독작업","특수폐기물","정기관리"]', 24, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- 매트리스 청소
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('004', '싱글 매트리스 청소', '싱글사이즈 매트리스 케어', '싱글', 80000, 60000, 25, '1시간', '["진드기제거","살균소독","얼룩제거","건조작업"]', 30, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('004', '퀸 매트리스 청소', '퀸사이즈 매트리스 케어', '퀸', 100000, 80000, 20, '1.5시간', '["심층청소","알러지케어","항균코팅","냄새제거"]', 31, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('004', '킹 매트리스 청소', '킹사이즈 매트리스 케어', '킹', 120000, 95000, 21, '2시간', '["프리미엄케어","진드기박멸","UV살균","1년보증"]', 32, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('004', '토퍼 청소', '매트리스 토퍼 전문청소', '전규격', 50000, 40000, 20, '30분', '["토퍼청소","살균처리","건조","보관팁"]', 33, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('004', '소파 청소', '패브릭/가죽 소파 청소', '3-4인용', 150000, 120000, 20, '2시간', '["소파청소","얼룩제거","가죽관리","방수코팅"]', 34, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- 세탁기 케어
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('005', '통돌이 세탁기 청소', '일반 통돌이 세탁기 케어', '10kg이하', 80000, 65000, 19, '1.5시간', '["분해청소","곰팡이제거","살균소독","고무패킹교체"]', 40, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('005', '드럼세탁기 청소', '드럼세탁기 전문 케어', '10kg이하', 100000, 80000, 20, '2시간', '["완전분해","내부청소","필터청소","건조기능점검"]', 41, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('005', '대용량 세탁기', '15kg이상 대용량 세탁기', '15kg이상', 120000, 100000, 17, '2.5시간', '["대용량청소","부품점검","성능최적화","보증서비스"]', 42, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('005', '건조기 청소', '의류건조기 청소 서비스', '전기종', 70000, 55000, 21, '1시간', '["필터청소","덕트청소","열교환기청소","성능점검"]', 43, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('005', '세탁기+건조기 패키지', '세탁기건조기 동시청소', '세트', 150000, 120000, 20, '3시간', '["동시청소","할인혜택","정기관리","무료점검"]', 44, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- 욕실 전문 시공
INSERT INTO `TB_PRODUCT` 
    (`SERVICE_CD`, `PRODUCT_NM`, `PRODUCT_DESC`, `AREA_TYPE`, `ORIGINAL_PRICE`, `SALE_PRICE`, `DISCOUNT_RATE`, `SERVICE_TIME`, `SERVICE_INCLUDES`, `DISPLAY_ORDER`, `DISPLAY_YN`, `SALE_YN`, `DEL_YN`, `REG_USER_ID`, `REG_DT`)
VALUES
    ('006', '욕실 곰팡이 제거', '욕실 곰팡이 완전제거', '1개소', 100000, 80000, 20, '2시간', '["곰팡이제거","실리콘교체","방수처리","항균코팅"]', 50, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('006', '욕실 줄눈 시공', '타일 줄눈 재시공', '1개소', 150000, 120000, 20, '3시간', '["줄눈제거","재시공","방수처리","미관개선"]', 51, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('006', '욕실 코팅', '욕실 전체 나노코팅', '1개소', 200000, 160000, 20, '4시간', '["나노코팅","발수코팅","오염방지","광택처리"]', 52, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('006', '욕실 리모델링', '욕실 부분 리모델링', '1개소', 500000, 400000, 20, '2일', '["타일교체","설비교체","방수공사","마감처리"]', 53, 'Y', 'Y', 'N', 'ADMIN', NOW()),
    ('006', '욕실 풀청소', '욕실 구석구석 청소', '1개소', 80000, 65000, 19, '1.5시간', '["전체청소","배수구청소","거울청소","위생도기"]', 54, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- -----------------------------------------------------------------------------
-- 5. TB_FILE - 파일 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_FILE` 
    (`FILE_REAL_NM`, `FILE_FAKE_NM`, `FILE_PATH`, `FILE_EXTSN`, `FILE_SIZE`, `RGS_DT`)
VALUES
    ('thumb-1.jpg', '0146373839623e56a2468d8f0b43c8225f57c0.jpg', '/upload/202501', 'jpg', 132906, NOW()),
    ('thumb-2.jpg', '0146372bb10f4ac9b5449093ff1387f4c8fac2.jpg', '/upload/202501', 'jpg', 104816, NOW()),
    ('thumb-3.jpg', '0146376b95d0e322fd4c34a8c9bb84c9557ad1.jpg', '/upload/202501', 'jpg', 117329, NOW()),
    ('case-1.jpg', '0146373839623e56a2468d8f0b43c8225f57c1.jpg', '/upload/202501', 'jpg', 245678, NOW()),
    ('case-2.jpg', '0146372bb10f4ac9b5449093ff1387f4c8fac3.jpg', '/upload/202501', 'jpg', 198765, NOW());

-- -----------------------------------------------------------------------------
-- 6. TB_CONSULTATION - 상담/예약 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_CONSULTATION` 
    (`NM`, `TEL1`, `TEL2`, `ZIP`, `ADRES1`, `ADRES2`, `SERVICE_CD`, `PRODUCT_NO`, `HOPE_DAY`, `INQRY_CN`, `REG_DT`, `PW`, `ANSWER`, `ANSWER_DT`, `ANSWER_ID`, `STTUS_CD`, `REQ_TYPE`)
VALUES
    ('김민수', '021234567', '01012345678', '12345', '서울시 강남구', '역삼동 123-45', '001', 1, '20250130', '에어컨 청소 예약하고 싶습니다.', NOW(), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', '안녕하세요. 예약 확정되었습니다.', NOW(), 'admin', '002', '002'),
    ('이영희', '029876543', '01098765432', '23456', '서울시 서초구', '서초동 678-90', '001', NULL, '20250201', '에어컨이 냄새가 나는데 청소하면 해결될까요?', DATE_ADD(NOW(), INTERVAL -1 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', '네, 청소하시면 냄새가 제거됩니다.', NOW(), 'admin', '003', '001'),
    ('박철수', '025551234', '01055512345', '34567', '경기도 성남시', '분당구 정자동', '004', 16, '20250205', '퀸사이즈 매트리스 청소 예약합니다.', DATE_ADD(NOW(), INTERVAL -2 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NULL, NULL, NULL, '001', '002'),
    ('최지은', '027774444', '01077744444', '45678', '인천시 연수구', '송도동 111-22', '005', NULL, '20250210', '세탁기에서 곰팡이 냄새가 납니다.', DATE_ADD(NOW(), INTERVAL -3 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NULL, NULL, NULL, '001', '001'),
    ('정수민', '023336666', '01033366666', '56789', '경기도 수원시', '영통구 매탄동', '006', 26, '20250215', '욕실 곰팡이 제거 서비스 문의', DATE_ADD(NOW(), INTERVAL -4 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', '견적서 발송했습니다.', DATE_ADD(NOW(), INTERVAL -3 DAY), 'manager', '002', '002'),
    ('강민정', '028889999', '01088899999', '67890', '서울시 송파구', '잠실동 333-44', '002', 6, '20250220', '에어컨 이전설치 가능한가요?', DATE_ADD(NOW(), INTERVAL -5 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', '네, 가능합니다. 일정 조율하겠습니다.', DATE_ADD(NOW(), INTERVAL -4 DAY), 'admin', '002', '002'),
    ('윤서연', '021112222', '01011122222', '78901', '서울시 마포구', '상암동 555-66', '003', 11, '20250225', '사무실 청소 정기계약 문의', DATE_ADD(NOW(), INTERVAL -6 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NULL, NULL, NULL, '001', '001'),
    ('임재현', '024445555', '01044455555', '89012', '경기도 고양시', '일산동구 백석동', '008', NULL, '20250228', '주방 후드청소 가격 문의', DATE_ADD(NOW(), INTERVAL -7 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NULL, NULL, NULL, '001', '001'),
    ('조민호', '026667777', '01066677777', '90123', '서울시 노원구', '상계동 777-88', '007', NULL, '20250302', '환풍기가 고장났는데 교체 가능한가요?', DATE_ADD(NOW(), INTERVAL -8 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', '교체 가능합니다. 현장 확인 후 진행하겠습니다.', DATE_ADD(NOW(), INTERVAL -7 DAY), 'manager', '002', '001'),
    ('한소희', '029990000', '01099900000', '01234', '부산시 해운대구', '우동 999-00', '001', 3, '20250305', '시스템 에어컨 2대 청소 예약', DATE_ADD(NOW(), INTERVAL -9 DAY), '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NULL, NULL, NULL, '001', '002');

-- -----------------------------------------------------------------------------
-- 7. TB_BOARD - 게시판 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_BOARD` 
    (`BOARD_GB`, `NOTICE_YN`, `BOARD_SJ`, `BOARD_CN`, `REG_NM`, `RGS_DT`, `SECRET_YN`, `SECRET_PW`)
VALUES
    ('001', 'Y', '2025년 설 연휴 휴무 안내', '2025년 설 연휴(1/27~1/30) 동안 휴무입니다. 긴급 문의는 카카오톡으로 부탁드립니다.', '관리자', NOW(), 'N', NULL),
    ('001', 'Y', '서비스 지역 확대 안내', '인천, 수원 지역까지 서비스를 확대하였습니다. 많은 이용 부탁드립니다.', '관리자', DATE_ADD(NOW(), INTERVAL -1 DAY), 'N', NULL),
    ('001', 'N', '홈페이지 리뉴얼 완료', '더욱 편리한 서비스를 위해 홈페이지를 리뉴얼하였습니다.', '관리자', DATE_ADD(NOW(), INTERVAL -2 DAY), 'N', NULL),
    ('002', 'N', '에어컨 청소 주기는 어떻게 되나요?', '일반적으로 1년에 1-2회 청소를 권장합니다. 사용 빈도가 높으면 더 자주 청소하시는 것이 좋습니다.', '관리자', DATE_ADD(NOW(), INTERVAL -3 DAY), 'N', NULL),
    ('002', 'N', '예약 취소는 어떻게 하나요?', '서비스 24시간 전까지 무료 취소 가능합니다. 고객센터로 연락 주시면 됩니다.', '관리자', DATE_ADD(NOW(), INTERVAL -4 DAY), 'N', NULL),
    ('002', 'N', '결제 방법은 어떤 것이 있나요?', '현금, 카드, 계좌이체 모두 가능합니다. 현장에서 결제하시면 됩니다.', '관리자', DATE_ADD(NOW(), INTERVAL -5 DAY), 'N', NULL),
    ('003', 'N', '에어컨 청소 전후 비교', '에어컨 청소 전후 사진입니다. 깨끗해진 모습을 확인하세요.', '관리자', DATE_ADD(NOW(), INTERVAL -6 DAY), 'N', NULL),
    ('003', 'N', '욕실 곰팡이 제거 사례', '욕실 곰팡이 제거 작업 사진입니다.', '관리자', DATE_ADD(NOW(), INTERVAL -7 DAY), 'N', NULL),
    ('004', 'N', '아파트 입주청소 완료', '30평 아파트 입주청소를 완료했습니다.', '시공팀', DATE_ADD(NOW(), INTERVAL -8 DAY), 'N', NULL),
    ('004', 'N', '사무실 정기청소 사례', '100평 사무실 정기청소 작업 사례입니다.', '시공팀', DATE_ADD(NOW(), INTERVAL -9 DAY), 'N', NULL),
    ('005', 'N', '서비스 문의드립니다', '견적 문의드립니다.', '김고객', DATE_ADD(NOW(), INTERVAL -10 DAY), 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K');

-- -----------------------------------------------------------------------------
-- 8. TB_CASE - 시공사례 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_CASE` 
    (`CASE_SJ`, `CASE_CN`, `REG_NM`, `FILE_SEQ`, `HASHTAG`, `RGS_DT`)
VALUES
    ('강남 아파트 에어컨 청소', '강남구 30평 아파트 에어컨 3대 청소 완료', '시공팀A', 1, '#에어컨,#강남,#아파트,#청소', NOW()),
    ('사무실 입주청소 완료', '여의도 200평 사무실 입주청소 작업', '시공팀B', 2, '#사무실,#입주청소,#여의도', DATE_ADD(NOW(), INTERVAL -1 DAY)),
    ('욕실 곰팡이 완전제거', '15년된 아파트 욕실 곰팡이 제거 및 코팅', '시공팀A', 3, '#욕실,#곰팡이,#코팅,#방수', DATE_ADD(NOW(), INTERVAL -2 DAY)),
    ('매트리스 진드기 제거', '킹사이즈 매트리스 진드기 제거 및 살균', '시공팀C', 4, '#매트리스,#진드기,#살균,#청소', DATE_ADD(NOW(), INTERVAL -3 DAY)),
    ('세탁기 분해청소', '10년된 드럼세탁기 완전분해 청소', '시공팀A', 5, '#세탁기,#분해청소,#드럼세탁기', DATE_ADD(NOW(), INTERVAL -4 DAY)),
    ('주방 후드 청소', '음식점 주방후드 및 덕트 청소', '시공팀D', 1, '#주방,#후드,#음식점,#청소', DATE_ADD(NOW(), INTERVAL -5 DAY)),
    ('환풍기 교체 설치', '욕실 환풍기 3대 교체 설치', '시공팀B', 2, '#환풍기,#욕실,#교체,#설치', DATE_ADD(NOW(), INTERVAL -6 DAY)),
    ('줄눈 시공 사례', '욕실 타일 줄눈 재시공 완료', '시공팀A', 3, '#줄눈,#타일,#욕실,#시공', DATE_ADD(NOW(), INTERVAL -7 DAY)),
    ('대형 에어컨 청소', '학원 시스템에어컨 10대 청소', '시공팀E', 4, '#시스템에어컨,#학원,#대형,#청소', DATE_ADD(NOW(), INTERVAL -8 DAY)),
    ('병원 특수청소', '치과 특수구역 청소 및 소독', '시공팀C', 5, '#병원,#치과,#특수청소,#소독', DATE_ADD(NOW(), INTERVAL -9 DAY)),
    ('신축 아파트 입주청소', '신축아파트 35평 입주청소', '시공팀A', 1, '#신축,#아파트,#입주청소', DATE_ADD(NOW(), INTERVAL -10 DAY)),
    ('카페 정기청소', '프랜차이즈 카페 월간 정기청소', '시공팀D', 2, '#카페,#정기청소,#프랜차이즈', DATE_ADD(NOW(), INTERVAL -11 DAY)),
    ('펜션 전체청소', '가평 펜션 5개동 전체 청소', '시공팀B', 3, '#펜션,#전체청소,#가평', DATE_ADD(NOW(), INTERVAL -12 DAY)),
    ('에어컨 대량청소', '오피스텔 50세대 에어컨 청소', '시공팀E', 4, '#오피스텔,#대량,#에어컨청소', DATE_ADD(NOW(), INTERVAL -13 DAY)),
    ('주방 리모델링 후 청소', '주방 리모델링 완료 후 청소작업', '시공팀C', 5, '#주방,#리모델링,#청소', DATE_ADD(NOW(), INTERVAL -14 DAY));

-- -----------------------------------------------------------------------------
-- 9. TB_FILE_RELATION - 파일 관계 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_FILE_RELATION` 
    (`FILE_TRGET_SE`, `FILE_TRGET_SEQ`, `FILE_SEQ`, `RGS_DT`)
VALUES
    ('BOARD', 7, 1, NOW()),
    ('BOARD', 7, 2, NOW()),
    ('BOARD', 8, 3, NOW()),
    ('CNSLT', 1, 4, NOW()),
    ('CNSLT', 1, 5, NOW()),
    ('CASE', 1, 1, NOW()),
    ('CASE', 2, 2, NOW()),
    ('CASE', 3, 3, NOW());

-- -----------------------------------------------------------------------------
-- 10. TB_REVIEW - 리뷰 데이터
-- -----------------------------------------------------------------------------
INSERT INTO `TB_REVIEW` 
    (`REVIEW_NM`, `REVIEW_CN`, `STAR_RATE`, `SERVICE_CD`, `PRODUCT_NO`, `SVC_DATE`, `DISP_YN`, `PW`, `RGS_DT`)
VALUES
    ('김민수', '에어컨 청소 정말 깔끔하게 해주셨어요. 냄새도 안나고 시원해졌습니다. 친절하신 기사님 덕분에 기분좋은 서비스였습니다.', 5, '001', 1, '20250115', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -20 DAY)),
    ('이영희', '사무실 청소 맡겼는데 구석구석 깨끗하게 해주셨습니다. 다만 시간이 예상보다 조금 더 걸렸네요.', 4, '003', 11, '20250120', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -19 DAY)),
    ('박철수', '매트리스 청소 완전 만족합니다! 진드기도 없어지고 알러지도 많이 좋아졌어요. 추천합니다!!', 5, '004', 17, '20250118', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -18 DAY)),
    ('최지은', '욕실 시공 정말 만족스럽습니다. 곰팡이 제거부터 코팅까지 완벽했어요.', 5, '006', 26, '20250122', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -17 DAY)),
    ('정수민', '세탁기 청소 처음 받아봤는데 이렇게 더러울 줄 몰랐네요. 깨끗하게 청소해주셔서 감사합니다.', 4, '005', 21, '20250125', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -15 DAY)),
    ('강민정', '주방 케어 서비스 받았는데 후드부터 가스레인지까지 새것처럼 반짝반짝해졌어요! 😊', 5, '008', NULL, '20250128', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -14 DAY)),
    ('손예진', '환풍기 설치 빠르고 깔끔하게 해주셨어요. 소음도 적고 좋습니다.', 4, '007', NULL, '20250130', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -13 DAY)),
    ('임재현', '에어컨 청소 두번째 이용입니다. 여전히 만족스럽네요. 정기적으로 이용하려구요.', 5, '001', 2, '20250201', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -12 DAY)),
    ('윤서연', '설치 서비스 이용했는데 기사님이 너무 친절하시고 꼼꼼하게 작업해주셨어요.', 5, '002', 6, '20250203', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -10 DAY)),
    ('조민호', '상가 청소 전체적으로 만족스럽습니다. 다만 예약 잡기가 조금 어려웠어요.', 4, '003', 12, '20250205', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -9 DAY)),
    ('문지원', '매트리스 청소 후 잠자리가 훨씬 쾌적해졌어요. 아이들 방도 같이 했는데 만족합니다.', 5, '004', 16, '20250208', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -8 DAY)),
    ('백승호', '욕실 곰팡이가 심했는데 말끔히 제거되었습니다. 가격대비 만족스러워요.', 4, '006', 25, '20250210', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -7 DAY)),
    ('한소희', '세탁기 청소하고 나니 빨래에서 냄새가 안나요! 정기적으로 받아야겠어요.', 5, '005', 22, '20250212', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -6 DAY)),
    ('오상현', '주방 청소 깔끔하게 잘 해주셨어요. 기름때가 말끔히 제거되었습니다.', 4, '008', NULL, '20250214', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -5 DAY)),
    ('서은지', '에어컨 3대 모두 청소했는데 일괄 할인도 해주시고 좋았어요.', 5, '001', 2, '20250216', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -4 DAY)),
    ('남주혁', '환풍기 교체 잘 해주셨습니다. 오래된 환풍기라 걱정했는데 깔끔하게 처리해주셨어요.', 4, '007', NULL, '20250218', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -3 DAY)),
    ('권나라', '매트리스 청소 너무 만족합니다. 아토피 있는 아이 때문에 했는데 확실히 좋아졌어요. 👍', 5, '004', 18, '20250220', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -2 DAY)),
    ('차은우', '사무실 정기 청소 계약했습니다. 첫 청소 만족스러웠어요. 앞으로도 기대됩니다.', 5, '003', 13, '20250222', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', DATE_ADD(NOW(), INTERVAL -1 DAY)),
    ('홍길동', '욕실 시공 완벽했습니다. 곰팡이 제거, 실리콘 교체, 코팅까지 모두 만족스럽네요.', 5, '006', 27, '20250224', 'Y', '$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K', NOW()),
    ('김태희', '세탁기 청소 강력 추천합니다! 10년된 세탁기인데 새것같아졌어요. 냄새도 완전 사라졌습니다.', 5, '005', 23, '20250225', 'Y', '$2a$10$Cxbc10SduAdyoE6ogl8cpeCdfwzjQlv2hNc03/oGcl09kvpIER3D6', NOW());

-- -----------------------------------------------------------------------------
-- 통계 데이터 확인 쿼리 (주석처리 - MariaDB 호환성)
-- SELECT 'Data Import Summary' AS 'Report';
-- SELECT 'TB_MANAGER' AS 'Table', COUNT(*) AS 'Records' FROM TB_MANAGER
-- UNION ALL SELECT 'TB_CMMN_CODE', COUNT(*) FROM TB_CMMN_CODE
-- UNION ALL SELECT 'TB_CATEGORY', COUNT(*) FROM TB_CATEGORY
-- UNION ALL SELECT 'TB_PRODUCT', COUNT(*) FROM TB_PRODUCT
-- UNION ALL SELECT 'TB_CONSULTATION', COUNT(*) FROM TB_CONSULTATION
-- UNION ALL SELECT 'TB_BOARD', COUNT(*) FROM TB_BOARD
-- UNION ALL SELECT 'TB_CASE', COUNT(*) FROM TB_CASE
-- UNION ALL SELECT 'TB_FILE', COUNT(*) FROM TB_FILE
-- UNION ALL SELECT 'TB_FILE_RELATION', COUNT(*) FROM TB_FILE_RELATION
-- UNION ALL SELECT 'TB_REVIEW', COUNT(*) FROM TB_REVIEW;

-- -----------------------------------------------------------------------------
-- 트랜잭션 커밋
-- -----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
SET AUTOCOMMIT = 1;

-- =============================================================================
-- 데이터 임포트 완료
-- =============================================================================
-- MariaDB 10.1.x 호환 데이터가 성공적으로 입력되었습니다.
-- 포함된 데이터:
-- - TB_MANAGER: 3명 (슈퍼관리자, 일반관리자, 운영매니저)
-- - TB_CMMN_CODE: 13개 코드 (진행상태, 구분, 게시판)
-- - TB_CATEGORY: 11개 서비스 카테고리
-- - TB_PRODUCT: 30개 상품 (에어컨, 설치, 시공, 매트리스, 세탁기, 욕실)
-- - TB_CONSULTATION: 10개 상담/예약
-- - TB_BOARD: 11개 게시물 (공지, FAQ, 포토갤러리, 시공사례)
-- - TB_CASE: 15개 시공사례
-- - TB_FILE: 5개 파일
-- - TB_FILE_RELATION: 8개 파일관계
-- - TB_REVIEW: 20개 리뷰
--
-- MariaDB 호환성:
-- - 모든 날짜 함수: MariaDB 호환 (DATE_ADD 사용)
-- - 문자셋: utf8mb4 (이모지 지원)
-- - 트랜잭션 처리 적용
-- - 외래키 순서 준수
-- - 타입: LONGTEXT (CLOB 미사용)
-- =============================================================================