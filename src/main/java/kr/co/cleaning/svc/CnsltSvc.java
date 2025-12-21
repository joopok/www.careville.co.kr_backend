package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.AESUtil;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.CnsltMapper;

@Service
public class CnsltSvc{

	private final static Logger log	= LoggerFactory.getLogger(CnsltSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	CnsltMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getCnsltList(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe", "CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		pageUtil.setViewRowCnt(10);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getCnsltCnt(paramMap));

		returnMap.put("rowNum"		,pageUtil.getRowNum());
		returnMap.put("pagination"	,pageUtil.getPaging("json"));		// pagination
		returnMap.put("list"		,mapper.getCnsltList(paramMap));	// 목록

		return returnMap;
	}

	public HashMap<String,Object> getCnsltView(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		paramMap.put("fileTrgetSe"	,"CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)
		paramMap.put("fileTrgetSeq"	,paramMap.get("cnsltSeq"));

		List<HashMap<String,Object>> files	= cmmnSvc.getFileList(paramMap);

		for(HashMap<String,Object> a : files){
			a.put("viewFileSeq", AESUtil.urlEnc(SUtils.nvl(a.get("fileSeq"))));
		}

		HashMap<String,Object> view = mapper.getCnsltView(paramMap);

		// 기존 데이터의 문의 내용에서 서비스/카테고리/요청사항 분리
		if (view != null) {
			parseViewContent(view);
		}

		returnMap.put("view"			,view);								// 상세
		returnMap.put("filesLst"		,files);							// 파일리스트
		returnMap.put("serviceCdLst"	,cmmnSvc.getServiceCdList());		// 서비스내용 코드
		returnMap.put("sttusCdLst"		,cmmnSvc.getCodeList("002"));		// 상담진행상태 코드

		return returnMap;
	}

	@Transactional(rollbackFor = Exception.class)
	public HashMap<String,Object> setCnsltUpd(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		Map<String, Object> sessionMap = sessionCmn.getLogonInfo();
		paramMap.put("answerId", sessionMap != null ? SUtils.nvl(sessionMap.get("mngrId")) : "admin");

		int cnsltCnt	= mapper.setCnsltUpd(paramMap);

		returnMap.put("isUpd", "Y");

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setCnsltReg(HttpServletRequest req,HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap	= new HashMap<String, Object>();

		// 디버그: 받은 파라미터 전체 출력
		log.debug("===== setCnsltReg received params =====");
		paramMap.forEach((key, value) -> log.debug("  {} = {}", key, value));
		log.debug("========================================");

		paramMap.put("fileTrgetSe", "CNSLT");	// 대상 구분(CNSLT:상담,BOARD:게시판)

		// 프론트엔드 파라미터를 DB 컬럼명으로 매핑 (BookingSvc 패턴 적용)
		mapFrontendParams(paramMap);

		// 문의 내용에서 서비스, 카테고리, 요청사항 분리
		parseFormattedContent(paramMap);

		// 추출된 서비스명/상품명으로 코드 조회
		lookupServiceAndProductCodes(paramMap);

		// 디버그: 매핑 후 파라미터 출력
		log.debug("===== After mapping =====");
		log.debug("  nm={}, tel1={}, tel2={}, email={}",
			paramMap.get("nm"), paramMap.get("tel1"), paramMap.get("tel2"), paramMap.get("email"));
		log.debug("=========================");

		int cnsltCnt	= mapper.setCnsltReg(paramMap);

		paramMap.put("fileTrgetSeq", SUtils.strToInt(paramMap.get("CNSLT_SEQ")));

		int isnertCnt	= cmmnSvc.setFileRelationInsert(paramMap);

		returnMap.put("isReg", "Y");

		return returnMap;
	}

	/**
	 * 프론트엔드 파라미터를 DB 컬럼명으로 매핑
	 * (customerName → nm, customerPhone → tel1/tel2 등)
	 */
	private void mapFrontendParams(HashMap<String,Object> paramMap) {
		// 이름 매핑 (다양한 키 지원)
		String[] nameKeys = {"customerName", "name", "userName", "clientName", "customer_name", "user_name"};
		for (String key : nameKeys) {
			if (paramMap.get("nm") == null && paramMap.get(key) != null) {
				paramMap.put("nm", paramMap.get(key));
				break;
			}
		}

		// 전화번호 매핑 (다양한 키 지원)
		String[] phoneKeys = {"customerPhone", "phone", "tel", "mobile", "phoneNumber", "telephone",
			"customer_phone", "phone_number", "contact", "cellPhone", "hp"};
		for (String key : phoneKeys) {
			if (paramMap.get("tel1") == null && paramMap.get(key) != null) {
				String phoneValue = SUtils.nvl(paramMap.get(key));
				if (!phoneValue.isEmpty()) {
					paramMap.put("tel1", phoneValue);
					paramMap.put("tel2", phoneValue);
					break;
				}
			}
		}

		// 이메일 매핑
		String[] emailKeys = {"customerEmail", "email", "userEmail", "customer_email", "mail"};
		for (String key : emailKeys) {
			if (paramMap.get("email") == null && paramMap.get(key) != null) {
				paramMap.put("email", paramMap.get(key));
				break;
			}
		}

		// 주소 매핑
		String[] addr1Keys = {"address1", "address", "addr1", "addr", "roadAddress", "jibunAddress"};
		for (String key : addr1Keys) {
			if (paramMap.get("adres1") == null && paramMap.get(key) != null) {
				paramMap.put("adres1", paramMap.get(key));
				break;
			}
		}

		String[] addr2Keys = {"address2", "addressDetail", "addr2", "detailAddress", "extraAddress"};
		for (String key : addr2Keys) {
			if (paramMap.get("adres2") == null && paramMap.get(key) != null) {
				paramMap.put("adres2", paramMap.get(key));
				break;
			}
		}

		// 우편번호 매핑
		String[] zipKeys = {"zipCode", "postcode", "zonecode", "postalCode", "postal_code", "zip_code"};
		for (String key : zipKeys) {
			if (paramMap.get("zip") == null && paramMap.get(key) != null) {
				paramMap.put("zip", paramMap.get(key));
				break;
			}
		}

		// 희망일자 매핑
		String[] dateKeys = {"bookingDate", "reserveDate", "hopeDate", "preferredDate", "date", "visitDate"};
		for (String key : dateKeys) {
			if (paramMap.get("hopeDay") == null && paramMap.get(key) != null) {
				paramMap.put("hopeDay", paramMap.get(key));
				break;
			}
		}

		// 희망시간 매핑
		String[] timeKeys = {"timeSlot", "reserveTime", "preferredTime", "time", "visitTime"};
		for (String key : timeKeys) {
			if (paramMap.get("hopeTime") == null && paramMap.get(key) != null) {
				paramMap.put("hopeTime", paramMap.get(key));
				break;
			}
		}

		// 문의내용 매핑
		String[] contentKeys = {"content", "message", "additionalRequests", "inquiry", "memo", "note", "description", "request"};
		for (String key : contentKeys) {
			if (paramMap.get("inqryCn") == null && paramMap.get(key) != null) {
				paramMap.put("inqryCn", paramMap.get(key));
				break;
			}
		}

		// 회사명 매핑
		String[] companyKeys = {"companyName", "company", "compNm", "corp", "corpName"};
		for (String key : companyKeys) {
			if (paramMap.get("compNm") == null && paramMap.get(key) != null) {
				paramMap.put("compNm", paramMap.get(key));
				break;
			}
		}

		// 요청 타입 기본값 (002: 상품 기반, 001: 서비스 기반)
		if (paramMap.get("reqType") == null) {
			if (paramMap.get("productNo") != null) {
				paramMap.put("reqType", "002");
			} else if (paramMap.get("serviceCd") != null) {
				paramMap.put("reqType", "001");
			} else {
				paramMap.put("reqType", "001"); // 기본값
			}
		}

		// tel2가 비어있으면 tel1 값 복사
		if (SUtils.nvl(paramMap.get("tel2")).isEmpty()) {
			paramMap.put("tel2", paramMap.get("tel1"));
		}

		// 로그: 매핑 결과가 null인 경우 경고
		if (paramMap.get("tel1") == null) {
			log.warn("tel1 is still null after mapping! Available keys: {}", paramMap.keySet());
		}
	}

	/**
	 * 문의 내용에서 서비스, 카테고리, 요청사항 등을 분리
	 * 형식: "서비스: xxx\n카테고리: xxx\n요청사항: xxx" 또는
	 *       "상품명: xxx\n가격: xxx원\n..." 등
	 */
	private void parseFormattedContent(HashMap<String,Object> paramMap) {
		String content = SUtils.nvl(paramMap.get("inqryCn"));
		if (content.isEmpty()) {
			// content, message 등 다른 키에서도 확인
			String[] contentKeys = {"content", "message", "inquiry", "memo"};
			for (String key : contentKeys) {
				if (paramMap.get(key) != null && !SUtils.nvl(paramMap.get(key)).isEmpty()) {
					content = SUtils.nvl(paramMap.get(key));
					break;
				}
			}
		}

		if (content.isEmpty()) {
			return;
		}

		log.debug("Parsing formatted content: {}", content);

		// 줄 단위로 분리
		String[] lines = content.split("\n");
		StringBuilder actualContent = new StringBuilder();
		String extractedService = null;
		String extractedCategory = null;
		String extractedProduct = null;
		String extractedPrice = null;
		String extractedTime = null;

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) continue;

			// "라벨: 값" 형태 파싱
			if (line.startsWith("서비스:") || line.startsWith("서비스 :")) {
				extractedService = line.replaceFirst("서비스\\s*:\\s*", "").trim();
			} else if (line.startsWith("카테고리:") || line.startsWith("카테고리 :")) {
				extractedCategory = line.replaceFirst("카테고리\\s*:\\s*", "").trim();
			} else if (line.startsWith("상품명:") || line.startsWith("상품명 :") || line.startsWith("상품:")) {
				extractedProduct = line.replaceFirst("상품(명)?\\s*:\\s*", "").trim();
			} else if (line.startsWith("가격:") || line.startsWith("가격 :")) {
				extractedPrice = line.replaceFirst("가격\\s*:\\s*", "").trim();
			} else if (line.startsWith("소요시간:") || line.startsWith("소요시간 :")) {
				extractedTime = line.replaceFirst("소요시간\\s*:\\s*", "").trim();
			} else if (line.startsWith("이메일:") || line.startsWith("이메일 :")) {
				String email = line.replaceFirst("이메일\\s*:\\s*", "").trim();
				if (paramMap.get("email") == null && !email.isEmpty()) {
					paramMap.put("email", email);
				}
			} else if (line.startsWith("희망시간대:") || line.startsWith("희망시간대 :")) {
				String timeSlot = line.replaceFirst("희망시간대\\s*:\\s*", "").trim();
				if (paramMap.get("hopeTime") == null && !timeSlot.isEmpty()) {
					paramMap.put("hopeTime", timeSlot);
				}
			} else if (line.startsWith("요청사항:") || line.startsWith("요청사항 :") ||
					   line.startsWith("추가요청사항:") || line.startsWith("추가요청사항 :")) {
				String request = line.replaceFirst("(추가)?요청사항\\s*:\\s*", "").trim();
				if (!request.isEmpty()) {
					actualContent.append(request).append("\n");
				}
			} else {
				// 라벨이 없는 일반 내용은 그대로 추가
				actualContent.append(line).append("\n");
			}
		}

		// 추출된 값들을 paramMap에 설정 (기존 값이 없는 경우에만)
		if (extractedService != null && !extractedService.isEmpty()) {
			// 서비스명으로 serviceCd 조회 시도
			if (paramMap.get("serviceCd") == null) {
				// serviceCd는 조회가 필요하므로 서비스명을 별도 저장
				paramMap.put("serviceNmInput", extractedService);
				log.debug("Extracted service name: {}", extractedService);
			}
		}

		if (extractedCategory != null && !extractedCategory.isEmpty()) {
			paramMap.put("categoryNmInput", extractedCategory);
			log.debug("Extracted category: {}", extractedCategory);
		}

		if (extractedProduct != null && !extractedProduct.isEmpty()) {
			paramMap.put("productNmInput", extractedProduct);
			log.debug("Extracted product: {}", extractedProduct);
			// 상품이 있으면 reqType을 002(예약)로 설정
			if (paramMap.get("reqType") == null) {
				paramMap.put("reqType", "002");
			}
		}

		// 실제 문의 내용만 inqryCn에 저장
		String actualContentStr = actualContent.toString().trim();
		if (!actualContentStr.isEmpty()) {
			paramMap.put("inqryCn", actualContentStr);
		} else if (extractedService != null || extractedProduct != null) {
			// 서비스/상품 정보만 있고 실제 요청사항이 없는 경우
			StringBuilder summary = new StringBuilder();
			if (extractedCategory != null) {
				summary.append("[").append(extractedCategory).append("] ");
			}
			if (extractedService != null) {
				summary.append(extractedService);
			} else if (extractedProduct != null) {
				summary.append(extractedProduct);
				if (extractedPrice != null) {
					summary.append(" (").append(extractedPrice).append(")");
				}
			}
			if (extractedTime != null) {
				summary.append(" - 소요시간: ").append(extractedTime);
			}
			paramMap.put("inqryCn", summary.toString());
		}

		log.debug("Parsed inqryCn: {}", paramMap.get("inqryCn"));
	}

	/**
	 * 상세 조회 시 문의 내용에서 서비스/카테고리/요청사항 분리 (기존 데이터 호환)
	 */
	private void parseViewContent(HashMap<String,Object> view) {
		String inqryCn = SUtils.nvl(view.get("inqryCn"));
		if (inqryCn.isEmpty()) return;

		// 이미 서비스명/상품명이 있으면 분리 불필요
		if (view.get("serviceNm") != null || view.get("productNm") != null) {
			// 하지만 inqryCn에 라벨이 포함되어 있으면 정리
			if (inqryCn.contains("서비스:") || inqryCn.contains("카테고리:") || inqryCn.contains("요청사항:")) {
				String[] lines = inqryCn.split("\n");
				StringBuilder actualContent = new StringBuilder();
				for (String line : lines) {
					line = line.trim();
					if (line.isEmpty()) continue;
					// 라벨 제거
					if (!line.startsWith("서비스:") && !line.startsWith("카테고리:") &&
						!line.startsWith("상품명:") && !line.startsWith("가격:") &&
						!line.startsWith("소요시간:") && !line.startsWith("이메일:") &&
						!line.startsWith("희망시간대:")) {
						if (line.startsWith("요청사항:") || line.startsWith("추가요청사항:")) {
							line = line.replaceFirst("(추가)?요청사항\\s*:\\s*", "");
						}
						if (!line.isEmpty()) {
							actualContent.append(line).append("\n");
						}
					}
				}
				view.put("inqryCn", actualContent.toString().trim());
			}
			return;
		}

		// 서비스명/상품명이 없는 경우 문의 내용에서 추출
		String[] lines = inqryCn.split("\n");
		StringBuilder actualContent = new StringBuilder();
		String extractedService = null;
		String extractedCategory = null;
		String extractedProduct = null;

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) continue;

			if (line.startsWith("서비스:") || line.startsWith("서비스 :")) {
				extractedService = line.replaceFirst("서비스\\s*:\\s*", "").trim();
			} else if (line.startsWith("카테고리:") || line.startsWith("카테고리 :")) {
				extractedCategory = line.replaceFirst("카테고리\\s*:\\s*", "").trim();
			} else if (line.startsWith("상품명:") || line.startsWith("상품명 :") || line.startsWith("상품:")) {
				extractedProduct = line.replaceFirst("상품(명)?\\s*:\\s*", "").trim();
			} else if (line.startsWith("요청사항:") || line.startsWith("요청사항 :") ||
					   line.startsWith("추가요청사항:") || line.startsWith("추가요청사항 :")) {
				String request = line.replaceFirst("(추가)?요청사항\\s*:\\s*", "").trim();
				if (!request.isEmpty()) {
					actualContent.append(request).append("\n");
				}
			} else if (!line.startsWith("가격:") && !line.startsWith("소요시간:") &&
					   !line.startsWith("이메일:") && !line.startsWith("희망시간대:")) {
				actualContent.append(line).append("\n");
			}
		}

		// 추출된 값을 view에 설정
		if (extractedService != null && !extractedService.isEmpty()) {
			view.put("parsedServiceNm", extractedService);
			// serviceNm이 없으면 대체
			if (view.get("serviceNm") == null) {
				view.put("serviceNm", extractedService);
			}
		}
		if (extractedCategory != null && !extractedCategory.isEmpty()) {
			view.put("parsedCategoryNm", extractedCategory);
		}
		if (extractedProduct != null && !extractedProduct.isEmpty()) {
			view.put("parsedProductNm", extractedProduct);
			if (view.get("productNm") == null) {
				view.put("productNm", extractedProduct);
			}
		}

		// 실제 요청 내용만 저장
		String actualContentStr = actualContent.toString().trim();
		if (!actualContentStr.isEmpty()) {
			view.put("inqryCn", actualContentStr);
		}
	}

	/**
	 * 추출된 서비스명/상품명으로 코드 조회
	 */
	private void lookupServiceAndProductCodes(HashMap<String,Object> paramMap) {
		// 서비스명으로 serviceCd 조회
		String serviceNmInput = SUtils.nvl(paramMap.get("serviceNmInput"));
		if (!serviceNmInput.isEmpty() && paramMap.get("serviceCd") == null) {
			try {
				String serviceCd = mapper.getServiceCdByName(serviceNmInput);
				if (serviceCd != null && !serviceCd.isEmpty()) {
					paramMap.put("serviceCd", serviceCd);
					paramMap.put("reqType", "001"); // 서비스 기반 상담
					log.debug("Found serviceCd: {} for serviceNm: {}", serviceCd, serviceNmInput);
				}
			} catch (Exception e) {
				log.warn("Failed to lookup serviceCd for: {}", serviceNmInput);
			}
		}

		// 상품명으로 productNo 조회
		String productNmInput = SUtils.nvl(paramMap.get("productNmInput"));
		if (!productNmInput.isEmpty() && paramMap.get("productNo") == null) {
			try {
				Integer productNo = mapper.getProductNoByName(productNmInput);
				if (productNo != null && productNo > 0) {
					paramMap.put("productNo", productNo);
					paramMap.put("reqType", "002"); // 상품 기반 예약
					log.debug("Found productNo: {} for productNm: {}", productNo, productNmInput);
				}
			} catch (Exception e) {
				log.warn("Failed to lookup productNo for: {}", productNmInput);
			}
		}
	}

}
