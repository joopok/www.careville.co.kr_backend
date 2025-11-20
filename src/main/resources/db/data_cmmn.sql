INSERT INTO TB_MANAGER(MNGR_ID,MNGR_PW,MNGR_NM,MNGR_NCNM,MNGR_TEL,MNGR_STTUS,RGS_DT)VALUES
('superadmin'	,'$2a$10$Cxbc10SduAdyoE6ogl8cpeCdfwzjQlv2hNc03/oGcl09kvpIER3D6'	,'홍길동'	,'슈퍼관리자'	,'01000000000','Y',NOW()),
('managera'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌1'		,'01000000000','Y',NOW()),
('managerb'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'김유신'	,'케어빌2'		,'01000000000','Y',NOW()),
('managerc'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'계백'	,'케어빌3'		,'01000000000','Y',NOW()),
('managerd'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'연개소문'	,'케어빌4'		,'01000000000','Y',NOW()),
('managere'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'을지문덕'	,'케어빌5'		,'01000000000','Y',NOW()),
('managerf'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'권율'	,'케어빌6'		,'01000000000','Y',NOW()),
('managerg'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'곽재우'	,'케어빌7'		,'01000000000','Y',NOW()),
('managerh'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'임경업'	,'케어빌8'		,'01000000000','Y',NOW()),
('manageri'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌9'		,'01000000000','Y',NOW()),
('managerj'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌10'	,'01000000000','Y',NOW()),
('managerk'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌11'	,'01000000000','Y',NOW()),
('managerl'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌12'	,'01000000000','Y',NOW()),
('managerm'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌13'	,'01000000000','Y',NOW()),
('managern'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌14'	,'01000000000','Y',NOW()),
('managero'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌15'	,'01000000000','Y',NOW()),
('managerp'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌16'	,'01000000000','Y',NOW()),
('managerq'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌17'	,'01000000000','Y',NOW()),
('managerr'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'이순신'	,'케어빌18'	,'01000000000','Y',NOW()),
('managers'		,'$2a$10$WiqMkVQG5I7qhnpguY/LKuX0yz5bnIaIDBa7xG0Sub3HELTN6h362'	,'김유신'	,'케어빌19'	,'01000000000','Y',NOW());


INSERT INTO TB_CMMN_CODE(GROUP_CD,GROUP_NM,CODE,CODE_NM,CODE_USE_YN)VALUES
('002'	,'상담,예약진행상태'	,'001'	,'접수'	,'Y'),
('002'	,'상담,예약진행상태'	,'002'	,'완료'	,'Y'),
('003'	,'상담,예약구분'		,'001'	,'상담'	,'Y'),
('003'	,'상담,예약구분'		,'002'	,'예약'	,'Y');


INSERT INTO TB_CONSULTATION(NM,TEL1,TEL2,ZIP,ADRES1,ADRES2,SERVICE_CD,PRODUCT_NO,HOPE_DAY,INQRY_CN,REG_DT,PW,ANSWER,ANSWER_DT,ANSWER_ID,STTUS_CD,REQ_TYPE)VALUES
 ('이름1' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 531호','001',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름2' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 532호','005',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름3' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 534호','005',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름4' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 535호','006',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름5' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 536호','001',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름6' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 537호','007',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름7' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 538호','004',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름8' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 539호','006',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'002','001')
,('이름9' ,'021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 540호','005',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,NOW(),null,'001','001')
,('이름10','021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 541호','002',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,null,null,'001','001')
,('이름11','021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 542호','003',null,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,null,null,'001','001')
,('이름12','021234124','01098741234','12345','서울특별시 마포구 대흥로 175','마포그랑자이 3215동 543호','003',1,'20250730','우갸갸갸',NOW(),'$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',null,null,null,'001','002');


INSERT INTO TB_CASE(CASE_SJ,SERVICE_CD,CASE_CN,REG_NM,FILE_SEQ,HASHTAG,RGS_DT)VALUES
('시공 사례 1'		,'001'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 2'		,'001'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 3'		,'001'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 4'		,'002'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 5'		,'002'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 6'		,'007'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 7'		,'007'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 8'		,'002'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 9'		,'007'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 10'	,'002'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 11'	,'009'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 12'	,'009'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 13'	,'002'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 14'	,'009'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 15'	,'002'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 16'	,'009'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 17'	,'002'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 18'	,'002'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 19'	,'009'	,'시공 사례'	,'슈퍼관리자'	,1	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 20'	,'002'	,'시공 사례'	,'슈퍼관리자'	,2	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 21'	,'009'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW()),
('시공 사례 22'	,'007'	,'시공 사례'	,'슈퍼관리자'	,1	,null	,NOW()),
('시공 사례 23'	,'002'	,'시공 사례'	,'슈퍼관리자'	,2	,''	,NOW()),
('줄눈 시공 사례'	,'002'	,'시공 사례'	,'슈퍼관리자'	,3	,'#현관,#화장실,#베란다,#부부욕실,#옥상'	,NOW());


INSERT INTO TB_FILE_RELATION(FILE_TRGET_SE,FILE_TRGET_SEQ,FILE_SEQ,RGS_DT)VALUES('CNSLT',12,1,NOW());
INSERT INTO TB_FILE_RELATION(FILE_TRGET_SE,FILE_TRGET_SEQ,FILE_SEQ,RGS_DT)VALUES('CNSLT',12,2,NOW());
INSERT INTO TB_FILE_RELATION(FILE_TRGET_SE,FILE_TRGET_SEQ,FILE_SEQ,RGS_DT)VALUES('CNSLT',12,3,NOW());

INSERT INTO TB_FILE(FILE_REAL_NM,FILE_FAKE_NM,FILE_PATH,FILE_EXTSN,FILE_SIZE,RGS_DT)VALUES('thumb-1.jpg'	,'0146373839623e56a2468d8f0b43c8225f57c0.jpg','C:\Users\nun\Downloads\careville\202508','jpg','132906',NOW());
INSERT INTO TB_FILE(FILE_REAL_NM,FILE_FAKE_NM,FILE_PATH,FILE_EXTSN,FILE_SIZE,RGS_DT)VALUES('thumb-2.jpg'	,'0146372bb10f4ac9b5449093ff1387f4c8fac2.jpg','C:\Users\nun\Downloads\careville\202508','jpg','104816',NOW());
INSERT INTO TB_FILE(FILE_REAL_NM,FILE_FAKE_NM,FILE_PATH,FILE_EXTSN,FILE_SIZE,RGS_DT)VALUES('thumb-2.jpg'	,'0146376b95d0e322fd4c34a8c9bb84c9557ad1.jpg','C:\Users\nun\Downloads\careville\202508','jpg','117329',NOW());

-- 작업후기 샘플 데이터
-- 다양한 비밀번호를 가진 리뷰들 (테스트용)
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('김민수','에어컨 청소 정말 깔끔하게 해주셨어요. 냄새도 안나고 시원해졌습니다. 친절하신 기사님 덕분에 기분좋은 서비스였습니다.',5,'001',NULL,'20250115','Y','$2a$10$xFqzLkNRa2J0hqPV3EwuseH3yvgqXf0E1fTKJrZ95xEVOHpEZH0De',DATE_SUB(NOW(),INTERVAL 20 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('이영희','사무실 청소 맡겼는데 구석구석 깨끗하게 해주셨습니다. 다만 시간이 예상보다 조금 더 걸렸네요.',4,'003',NULL,'20250120','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 19 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('박철수','매트리스 청소 완전 만족합니다! 진드기도 없어지고 알러지도 많이 좋아졌어요. 추천합니다!!',5,'004',NULL,'20250118','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 18 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('최지은','욕실 시공 정말 만족스럽습니다. 곰팡이 제거부터 코팅까지 완벽했어요.',5,'006',NULL,'20250122','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 17 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('정수민','세탁기 청소 처음 받아봤는데 이렇게 더러울 줄 몰랐네요. 깨끗하게 청소해주셔서 감사합니다.',4,'005',NULL,'20250125','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 15 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('강민정','주방 케어 서비스 받았는데 후드부터 가스레인지까지 새것처럼 반짝반짝해졌어요!',5,'008',NULL,'20250128','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 14 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('손예진','환풍기 설치 빠르고 깔끔하게 해주셨어요. 소음도 적고 좋습니다.',4,'007',NULL,'20250130','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 13 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('임재현','에어컨 청소 두번째 이용입니다. 여전히 만족스럽네요. 정기적으로 이용하려구요.',5,'001',NULL,'20250201','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 12 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('윤서연','설치 서비스 이용했는데 기사님이 너무 친절하시고 꼼꼼하게 작업해주셨어요.',5,'002',NULL,'20250203','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 10 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('조민호','상가 청소 전체적으로 만족스럽습니다. 다만 예약 잡기가 조금 어려웠어요.',4,'003',NULL,'20250205','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 9 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('문지원','매트리스 청소 후 잠자리가 훨씬 쾌적해졌어요. 아이들 방도 같이 했는데 만족합니다.',5,'004',NULL,'20250208','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 8 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('백승호','욕실 곰팡이가 심했는데 말끔히 제거되었습니다. 가격대비 만족스러워요.',4,'006',NULL,'20250210','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 7 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('한소희','세탁기 청소하고 나니 빨래에서 냄새가 안나요! 정기적으로 받아야겠어요.',5,'005',NULL,'20250212','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 6 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('오상현','주방 청소 깔끔하게 잘 해주셨어요. 기름때가 말끔히 제거되었습니다.',4,'008',NULL,'20250214','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 5 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('서은지','에어컨 3대 모두 청소했는데 일괄 할인도 해주시고 좋았어요.',5,'001',NULL,'20250216','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 4 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('남주혁','환풍기 교체 잘 해주셨습니다. 오래된 환풍기라 걱정했는데 깔끔하게 처리해주셨어요.',4,'007',NULL,'20250218','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 3 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('권나라','매트리스 청소 너무 만족합니다. 아토피 있는 아이 때문에 했는데 확실히 좋아졌어요.',5,'004',NULL,'20250220','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 2 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('차은우','사무실 정기 청소 계약했습니다. 첫 청소 만족스러웠어요. 앞으로도 기대됩니다.',5,'003',NULL,'20250222','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',DATE_SUB(NOW(),INTERVAL 1 DAY));
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('홍길동','욕실 시공 완벽했습니다. 곰팡이 제거, 실리콘 교체, 코팅까지 모두 만족스럽네요.',5,'006',NULL,'20250224','Y','$2a$10$A.JVF6n8gO3hPmEbDOjj1OPHdPU24dgpYrv0pz3ioPgDhAMztNJ5K',NOW());
INSERT INTO TB_REVIEW(REVIEW_NM,REVIEW_CN,STAR_RATE,SERVICE_CD,PRODUCT_NO,SVC_DATE,DISP_YN,PW,RGS_DT)VALUES('김태희','세탁기 청소 강력 추천합니다! 10년된 세탁기인데 새것같아졌어요. 냄새도 완전 사라졌습니다.',5,'005',2,'20250225','Y','$2a$10$Cxbc10SduAdyoE6ogl8cpeCdfwzjQlv2hNc03/oGcl09kvpIER3D6',NOW());


-- 서비스 카테고리 초기 데이터
-- 기존 데이터 삭제 (재실행 가능하도록)
-- 외래 키 제약 조건 때문에 TB_PRODUCT 먼저 삭제
DELETE FROM TB_PRODUCT WHERE 1=1;
DELETE FROM TB_CATEGORY WHERE 1=1;

-- 서비스 카테고리 데이터 삽입
INSERT INTO TB_CATEGORY (SERVICE_CD, SERVICE_NM, SERVICE_ORDER, USE_YN, DEL_YN, REG_USER_ID, REG_DT) VALUES 
('001', '에어컨 케어 및 세척', 1, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('002', '설치/교체 서비스', 2, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('003', '상가/사무실 시공', 3, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('004', '메트리스 청소(케어)', 4, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('005', '세탁기 케어', 5, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('006', '욕실 전문 시공', 6, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('007', '환풍기 설치', 7, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('008', '프리미엄 주방케어', 8, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('009', '특수청소', 9, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('010', '주방상판', 10, 'Y', 'N', 'system', CURRENT_TIMESTAMP),
('011', '층간소음매트', 11, 'Y', 'N', 'system', CURRENT_TIMESTAMP);




-- 상품 데이터 30개 삽입
-- 가정집 청소 (001)
INSERT INTO TB_PRODUCT (SERVICE_CD, PRODUCT_NM, PRODUCT_DESC, AREA_TYPE, ORIGINAL_PRICE, SALE_PRICE, DISCOUNT_RATE, SERVICE_TIME, SERVICE_INCLUDES, DISPLAY_ORDER, DISPLAY_YN, SALE_YN, DEL_YN, REG_USER_ID, REG_DT) VALUES
('001', '아파트 기본 청소', '20-30평형 아파트 전문 청소 서비스입니다. 거실, 방, 주방, 욕실 등 전 구역을 깨끗하게 청소해드립니다.', '20-30평형', 100000, 80000, 20, '3-4시간', '["전 구역 청소 (거실, 방, 주방, 욕실)","바닥 및 걸레질","먼지 제거 및 진공청소","주방 기기 외부 청소","욕실 타일 및 위생도기 청소"]', 1, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '아파트 프리미엄 청소', '30-40평형 아파트 고급 청소 서비스입니다.', '30-40평형', 150000, 120000, 20, '4-5시간', '["전 구역 심층 청소","베란다 청소 포함","에어컨 필터 청소","침구류 정리","냉장고 외부 청소","창틀 및 창문 청소"]', 2, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '소형 아파트 청소', '15평 이하 소형 아파트 맞춤 청소입니다.', '15평 이하', 70000, 60000, 14, '2-3시간', '["원룸/투룸 전문 청소","욕실 특별 관리","주방 집중 청소","바닥 왁싱"]', 3, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '빌라 청소', '빌라 및 연립주택 전문 청소 서비스입니다.', '20-30평형', 90000, 75000, 17, '3-4시간', '["전 구역 청소","계단 청소","현관 특별 관리","발코니 청소"]', 4, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '주택 청소', '단독주택 맞춤형 청소 서비스입니다.', '30평 이상', 180000, 150000, 17, '5-6시간', '["마당 청소","다락방 정리","창고 정리","외벽 먼지 제거","정원 관리"]', 5, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '원룸 집중 청소', '원룸 특화 청소 서비스입니다.', '10평 이하', 50000, 40000, 20, '2시간', '["원룸 전체 청소","곰팡이 제거","환기구 청소","싱크대 특별 관리"]', 6, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '펜트하우스 청소', '고급 펜트하우스 전문 청소입니다.', '50평 이상', 300000, 250000, 17, '6-8시간', '["전 구역 프리미엄 청소","대리석 관리","고급 가구 관리","테라스 청소","와인셀러 정리"]', 7, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('001', '복층 아파트 청소', '복층 구조 아파트 전문 청소입니다.', '40-50평형', 200000, 160000, 20, '5-6시간', '["1층/2층 전체 청소","계단 특별 관리","높은 천장 먼지 제거","샹들리에 청소"]', 8, 'Y', 'Y', 'N', 'ADMIN', NOW()),

-- 사무실 청소 (002)
('002', '소규모 사무실 청소', '50평 이하 소규모 사무실 청소입니다.', '50평 이하', 120000, 100000, 17, '3-4시간', '["사무 공간 청소","회의실 정리","탕비실 청소","화장실 청소","쓰레기 처리"]', 10, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '중규모 사무실 청소', '100평 규모 사무실 청소 서비스입니다.', '50-100평', 200000, 170000, 15, '4-5시간', '["전 구역 청소","카펫 청소","창문 청소","책상 정리","공용 공간 관리"]', 11, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '대규모 사무실 청소', '200평 이상 대형 사무실 청소입니다.', '200평 이상', 400000, 350000, 13, '6-8시간', '["층별 청소","서버실 청소","대회의실 관리","로비 청소","주차장 입구 관리"]', 12, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '코워킹 스페이스 청소', '공유 오피스 전문 청소 서비스입니다.', '100-150평', 250000, 200000, 20, '5-6시간', '["개별 부스 청소","라운지 관리","회의실 청소","카페테리아 청소","Phone Booth 청소"]', 13, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '스타트업 사무실 청소', '스타트업 맞춤 청소 서비스입니다.', '30-50평', 80000, 65000, 19, '2-3시간', '["오픈 오피스 청소","휴게실 관리","게임룸 정리","간식존 청소"]', 14, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '병원 사무실 청소', '의료 시설 사무 공간 청소입니다.', '100평', 180000, 150000, 17, '4-5시간', '["위생 관리 강화","대기실 청소","상담실 정리","의료 폐기물 분리"]', 15, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('002', '법무법인 사무실 청소', '법무법인 전문 청소 서비스입니다.', '150평', 280000, 240000, 14, '5-6시간', '["서류 보관실 정리","상담실 청소","도서관 관리","VIP 라운지 청소"]', 16, 'Y', 'Y', 'N', 'ADMIN', NOW()),

-- 이사 청소 (003)
('003', '입주 청소', '새 집 입주 전 청소 서비스입니다.', '30평형', 150000, 120000, 20, '4-5시간', '["전체 소독","바닥 왁싱","욕실 코팅","주방 청소","베란다 청소","현관 정리"]', 20, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '이사 후 청소', '이사 나간 후 원상복구 청소입니다.', '25평형', 130000, 100000, 23, '3-4시간', '["벽지 얼룩 제거","바닥 복구","못자국 처리","원상 복구","쓰레기 처리"]', 21, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '신축 아파트 입주 청소', '신축 아파트 입주 특화 청소입니다.', '35평형', 180000, 150000, 17, '5-6시간', '["건축 먼지 제거","유해물질 제거","새집 증후군 방지","환기 시스템 청소"]', 22, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '리모델링 후 청소', '리모델링 완료 후 청소 서비스입니다.', '40평형', 200000, 170000, 15, '5-6시간', '["공사 먼지 제거","페인트 자국 제거","타일 줄눈 청소","새 가구 정리"]', 23, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '전세 퇴거 청소', '전세 보증금 회수를 위한 청소입니다.', '30평형', 140000, 110000, 21, '4시간', '["완벽한 원상복구","보증금 회수 지원","체크리스트 점검","사진 촬영 서비스"]', 24, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '월세 이사 청소', '월세 이사 전문 청소 서비스입니다.', '20평형', 90000, 70000, 22, '3시간', '["빠른 청소","간단 보수","열쇠 인계 준비","체크아웃 지원"]', 25, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('003', '오피스텔 입주 청소', '오피스텔 입주 맞춤 청소입니다.', '15평형', 80000, 65000, 19, '2-3시간', '["컴팩트 공간 최적화","붙박이장 청소","시스템 가구 관리","현관 소독"]', 26, 'Y', 'Y', 'N', 'ADMIN', NOW()),

-- 특수 청소 (004)
('004', '곰팡이 제거 청소', '곰팡이 전문 제거 서비스입니다.', '전체', 100000, 85000, 15, '3-4시간', '["곰팡이 완전 제거","항균 코팅","습도 관리 컨설팅","재발 방지 처리"]', 30, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '화재 후 청소', '화재 피해 복구 청소 서비스입니다.', '피해 구역', 300000, 250000, 17, '1-2일', '["그을음 제거","냄새 제거","손상 물품 정리","보험 서류 지원"]', 31, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '수해 복구 청소', '침수 피해 복구 전문 청소입니다.', '피해 구역', 250000, 200000, 20, '1-2일', '["물기 제거","건조 작업","살균 소독","곰팡이 방지"]', 32, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '쓰레기집 청소', '적체물 제거 전문 서비스입니다.', '전체', 500000, 400000, 20, '2-3일', '["대량 폐기물 처리","해충 방제","완전 소독","심리 상담 연계"]', 33, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '애완동물 청소', '반려동물 전문 청소 서비스입니다.', '전체', 120000, 100000, 17, '3-4시간', '["털 제거","냄새 제거","알레르기 유발물질 제거","항균 처리"]', 34, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '알레르기 케어 청소', '알레르기 환자를 위한 특수 청소입니다.', '전체', 150000, 130000, 13, '4-5시간', '["진드기 제거","미세먼지 제거","헤파필터 청소","친환경 세제 사용"]', 35, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '소독 방역 청소', '전문 소독 방역 서비스입니다.', '전체', 100000, 80000, 20, '2-3시간', '["바이러스 소독","해충 방제","공기 정화","위생 인증서 발급"]', 36, 'Y', 'Y', 'N', 'ADMIN', NOW()),

('004', '에어컨 전문 청소', '에어컨 분해 청소 서비스입니다.', '대당', 50000, 40000, 20, '1시간', '["완전 분해 청소","필터 교체","냉매 점검","살균 소독"]', 37, 'Y', 'Y', 'N', 'ADMIN', NOW());

-- TB_PRODUCT 테이블 컬럼 타입 변경 (기존 테이블이 CLOB로 생성된 경우)
ALTER TABLE TB_PRODUCT MODIFY COLUMN PRODUCT_DESC LONGTEXT;
ALTER TABLE TB_PRODUCT MODIFY COLUMN SERVICE_INCLUDES LONGTEXT;