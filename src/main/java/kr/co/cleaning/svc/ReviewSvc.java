package kr.co.cleaning.svc;

import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.cleaning.core.config.KFException;
import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.ReviewMapper;

@Service
public class ReviewSvc {

	private final static Logger log = LoggerFactory.getLogger(ReviewSvc.class);

	@Autowired
	SessionCmn sessionCmn;

	@Autowired
	ReviewMapper mapper;

	@Autowired
	CmmnSvc cmmnSvc;

	@Autowired
	PageUtil pageUtil;

	public HashMap<String,Object> getReviewList(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		// limitViewRowCnt가 이미 설정된 경우 그 값을 사용, 아니면 기본값 10
		int viewRowCnt = 10;
		if (paramMap.get("limitViewRowCnt") != null) {
			viewRowCnt = Integer.parseInt(paramMap.get("limitViewRowCnt").toString());
		}
		pageUtil.setViewRowCnt(viewRowCnt);

		// pagination setting
		pageUtil.setCurrPage(paramMap);
		pageUtil.setTotalRowCnt(mapper.getReviewCnt(paramMap));

		returnMap.put("rowNum"			,pageUtil.getRowNum());
		returnMap.put("pagination"		,pageUtil.getPaging("json"));
		returnMap.put("list"			,mapper.getReviewList(paramMap));
		returnMap.put("serviceCdLst"	,cmmnSvc.getServiceCdList()); 		// 서비스내용 코드
		returnMap.put("productCdList"	,cmmnSvc.getProductCdList()); 		// 삼품 코드 목록

		return returnMap;
	}

	public HashMap<String,Object> getReviewView(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		returnMap.put("view"			,mapper.getReviewView(paramMap));
		returnMap.put("serviceCdLst"	,cmmnSvc.getServiceCdList()); // 서비스내용 코드

		return returnMap;
	}

	public HashMap<String,Object> getReviewViewWithPasswordCheck(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		log.info("[리뷰 조회] 시작 - reviewSeq: {}", paramMap.get("reviewSeq"));

		// 먼저 비밀번호 포함된 리뷰 정보 조회
		HashMap<String,Object> reviewInfo = mapper.getReviewViewWithPassword(paramMap);

		if(reviewInfo == null) {
			log.warn("[리뷰 조회] 실패 - 존재하지 않는 리뷰: {}", paramMap.get("reviewSeq"));
			returnMap.put("success", false);
			returnMap.put("message", "존재하지 않는 리뷰입니다.");
			return returnMap;
		}

		// 비밀번호가 설정된 리뷰인 경우
		if(reviewInfo.get("pw") != null && !SUtils.nvl(reviewInfo.get("pw")).equals("")) {
			log.debug("[리뷰 조회] 비밀번호가 설정된 리뷰 - reviewSeq: {}", paramMap.get("reviewSeq"));

			// 요청에 비밀번호가 없으면 에러
			if(paramMap.get("pw") == null || SUtils.nvl(paramMap.get("pw")).equals("")) {
				log.info("[리뷰 조회] 비밀번호 필요 - reviewSeq: {}", paramMap.get("reviewSeq"));
				returnMap.put("success", false);
				returnMap.put("message", "비밀번호가 필요한 리뷰입니다.");
				returnMap.put("requirePassword", true);
				return returnMap;
			}

			// 비밀번호 검증
			String inputPassword = SUtils.nvl(paramMap.get("pw"));
			log.debug("[리뷰 조회] 비밀번호 검증 시작 - reviewSeq: {}, 입력 비밀번호 길이: {}",
				paramMap.get("reviewSeq"), inputPassword.length());

			boolean passwordMatch = BCrypt.checkpw(inputPassword, SUtils.nvl(reviewInfo.get("pw")));

			if(!passwordMatch) {
				log.warn("[리뷰 조회] 비밀번호 불일치 - reviewSeq: {}", paramMap.get("reviewSeq"));
				returnMap.put("success", false);
				returnMap.put("message", "비밀번호가 일치하지 않습니다.");
				return returnMap;
			}

			log.info("[리뷰 조회] 비밀번호 검증 성공 - reviewSeq: {}", paramMap.get("reviewSeq"));
		}

		// 비밀번호 제거 후 반환
		reviewInfo.remove("pw");

		log.info("[리뷰 조회] 성공 - reviewSeq: {}, 작성자: {}",
			paramMap.get("reviewSeq"), reviewInfo.get("reviewNm"));

		returnMap.put("success", true);
		returnMap.put("view", reviewInfo);
		returnMap.put("serviceCdLst", cmmnSvc.getServiceCdList()); // 서비스내용 코드

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setReviewReg(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		// 비밀번호 암호화 (수정/삭제용)
		if(paramMap.get("pw") != null && !SUtils.nvl(paramMap.get("pw")).equals("")) {
			paramMap.put("pw", BCrypt.hashpw(SUtils.nvl(paramMap.get("pw")), BCrypt.gensalt()));
		}

		// 디폴트 값 설정
		if(paramMap.get("dispYn") == null || SUtils.nvl(paramMap.get("dispYn")).equals("")) {
			paramMap.put("dispYn", "Y");
		}

		int reviewCnt = mapper.setReviewReg(paramMap);

		returnMap.put("isReg", reviewCnt > 0 ? "Y" : "N");
		returnMap.put("reviewSeq", paramMap.get("REVIEW_SEQ"));

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setReviewUpd(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		log.info("[리뷰 수정] 시작 - reviewSeq: {}, 수정자: {}",
			paramMap.get("reviewSeq"), paramMap.get("reviewNm"));

		// 먼저 리뷰가 존재하는지 확인 (비밀번호 포함해서 조회)
		HashMap<String,Object> reviewInfo = mapper.getReviewViewWithPassword(paramMap);

		if(reviewInfo == null) {
			log.warn("[리뷰 수정] 실패 - 존재하지 않는 리뷰: {}", paramMap.get("reviewSeq"));
			returnMap.put("success", false);
			returnMap.put("isUpd", "N");
			returnMap.put("message", "존재하지 않는 리뷰입니다.");
			returnMap.put("passwordValid", false);
			return returnMap;
		}

		// 관리자일 경우 비밀번호를 확인 하지 않음
		if(!sessionCmn.isLogon()){
			// 비밀번호가 설정된 리뷰인 경우 검증 필요
			if(reviewInfo.get("pw") != null && !SUtils.nvl(reviewInfo.get("pw")).equals("")) {
				// 비밀번호가 제공되지 않은 경우
				if(paramMap.get("pw") == null || SUtils.nvl(paramMap.get("pw")).equals("")) {
					returnMap.put("success", false);
					returnMap.put("isUpd", "N");
					returnMap.put("message", "비밀번호가 필요한 리뷰입니다.");
					returnMap.put("requirePassword", true);
					returnMap.put("passwordValid", false);
					return returnMap;
				}

				// 비밀번호 검증
				String inputPassword = SUtils.nvl(paramMap.get("pw"));
				log.debug("[review_update] 비밀번호 검증 시작 - reviewSeq={}", paramMap.get("reviewSeq"));

				boolean passwordMatch = BCrypt.checkpw(inputPassword, SUtils.nvl(reviewInfo.get("pw")));
				if(!passwordMatch) {
					log.warn("[리뷰 수정] 비밀번호 불일치 - reviewSeq: {}", paramMap.get("reviewSeq"));
					returnMap.put("success", false);
					returnMap.put("isUpd", "N");
					returnMap.put("message", "비밀번호가 일치하지 않습니다.");
					returnMap.put("passwordValid", false);
					return returnMap;
				}

				// 비밀번호 검증 성공 - 암호화된 비밀번호로 설정 (업데이트 조건용)
				log.info("[리뷰 수정] 비밀번호 검증 성공 - reviewSeq: {}", paramMap.get("reviewSeq"));
				paramMap.put("pw", reviewInfo.get("pw"));
				returnMap.put("passwordValid", true);
			} else {
				// 비밀번호가 없는 리뷰
				returnMap.put("passwordValid", true);
			}
		}

		// 서비스 날짜 포맷 처리
		if (reviewInfo.get("svcDate") != null) {
			paramMap.put("svcDate", reviewInfo.get("svcDate").toString().replaceAll("-", ""));
		}
		log.debug("[review_update] svcDate 처리 완료 - reviewSeq={}", paramMap.get("reviewSeq"));

		// 리뷰 수정 실행
		int reviewCnt = mapper.setReviewUpd(paramMap);

		if(reviewCnt > 0) {
			log.info("[리뷰 수정] 성공 - reviewSeq: {}, 수정된 행 수: {}", paramMap.get("reviewSeq"), reviewCnt);
			returnMap.put("success", true);
			returnMap.put("isUpd", "Y");
			returnMap.put("message", "리뷰가 성공적으로 수정되었습니다.");
		} else {
			log.error("[리뷰 수정] 실패 - reviewSeq: {}, 수정된 행 수: 0", paramMap.get("reviewSeq"));
			returnMap.put("success", false);
			returnMap.put("isUpd", "N");
			returnMap.put("message", "리뷰 수정에 실패했습니다.");
		}

		return returnMap;
	}


	@Transactional
	public HashMap<String,Object> setReviewDispUpd(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		// 리뷰 수정 실행
		int reviewCnt = mapper.setReviewDispUpd(paramMap);

		if(reviewCnt == 0){
			throw new KFException("수정되지 않았습니다.");
		}

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setReviewDispUpdAll(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		// 일괄 노출 상태 변경
		int reviewCnt = mapper.setReviewDispUpdAll(paramMap);

		returnMap.put("updatedCount", reviewCnt);
		returnMap.put("success", true);
		returnMap.put("message", reviewCnt + "개의 리뷰가 업데이트되었습니다.");

		return returnMap;
	}

	@Transactional
	public HashMap<String,Object> setReviewDel(HttpServletRequest req, HashMap<String,Object> paramMap) throws Exception {

		HashMap<String,Object> returnMap = new HashMap<String, Object>();

		log.info("[리뷰 삭제] 시작 - reviewSeq: {}", paramMap.get("reviewSeq"));

		// 먼저 리뷰가 존재하는지 확인 (비밀번호 포함해서 조회)
		HashMap<String,Object> reviewInfo = mapper.getReviewViewWithPassword(paramMap);

		if(reviewInfo == null) {
			returnMap.put("success", false);
			returnMap.put("isDel", "N");
			returnMap.put("message", "존재하지 않는 리뷰입니다.");
			returnMap.put("passwordValid", false);
			return returnMap;
		}

		// 관리자일 경우 비밀번호를 확인 하지 않음
		if(!sessionCmn.isLogon()){
			// 비밀번호가 설정된 리뷰인 경우 검증 필요
			if(reviewInfo.get("pw") != null && !SUtils.nvl(reviewInfo.get("pw")).equals("")) {
				// 비밀번호가 제공되지 않은 경우
				if(paramMap.get("pw") == null || SUtils.nvl(paramMap.get("pw")).equals("")) {
					returnMap.put("success", false);
					returnMap.put("isDel", "N");
					returnMap.put("message", "비밀번호가 필요한 리뷰입니다.");
					returnMap.put("requirePassword", true);
					returnMap.put("passwordValid", false);
					return returnMap;
				}

				// 비밀번호 검증
				String inputPassword = SUtils.nvl(paramMap.get("pw"));
				log.debug("[리뷰 삭제] 비밀번호 검증 시작 - reviewSeq: {}", paramMap.get("reviewSeq"));

				boolean passwordMatch = BCrypt.checkpw(inputPassword, SUtils.nvl(reviewInfo.get("pw")));
				if(!passwordMatch) {
					log.warn("[리뷰 삭제] 비밀번호 불일치 - reviewSeq: {}", paramMap.get("reviewSeq"));
					returnMap.put("success", false);
					returnMap.put("isDel", "N");
					returnMap.put("message", "비밀번호가 일치하지 않습니다.");
					returnMap.put("passwordValid", false);
					return returnMap;
				}

				// 비밀번호 검증 성공 - 암호화된 비밀번호로 설정 (삭제 조건용)
				log.info("[리뷰 삭제] 비밀번호 검증 성공 - reviewSeq: {}", paramMap.get("reviewSeq"));
				paramMap.put("pw", reviewInfo.get("pw"));
				returnMap.put("passwordValid", true);
			} else {
				// 비밀번호가 없는 리뷰
				returnMap.put("passwordValid", true);
			}
		}

		// 리뷰 삭제 실행
		int reviewCnt = mapper.setReviewDel(paramMap);

		if(reviewCnt > 0) {
			log.info("[리뷰 삭제] 성공 - reviewSeq: {}, 삭제된 행 수: {}", paramMap.get("reviewSeq"), reviewCnt);
			returnMap.put("success", true);
			returnMap.put("isDel", "Y");
			returnMap.put("message", "리뷰가 성공적으로 삭제되었습니다.");
		} else {
			log.error("[리뷰 삭제] 실패 - reviewSeq: {}, 삭제된 행 수: 0", paramMap.get("reviewSeq"));
			returnMap.put("success", false);
			returnMap.put("isDel", "N");
			returnMap.put("message", "리뷰 삭제에 실패했습니다.");
		}

		return returnMap;
	}
}