package kr.co.cleaning.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.ReviewSvc;

@Controller
@Tag(name = "Review API", description = "작업후기 관리 API")
public class ReviewCtrl {

	private static final Logger log = LoggerFactory.getLogger(ReviewCtrl.class);

	@Autowired
	ReviewSvc svc;

	@Autowired
	CmmnSvc CmmnSvc;

	@RequestMapping("/apage/review0{pageNum}.do")
	public String pageView(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, @PathVariable String pageNum, ModelMap modelMap) throws Exception {
		/*
		 * 10 : list
		 * 20 : view
		 * 30 : register
		 * 40 : modify
		 * */
		String pageNumber = pageNum;

		modelMap.addAttribute("searchMap", paramMap.clone());

		if(pageNum.equals("10")){
			modelMap.addAttribute("svcCdList", CmmnSvc.getServiceCdList());

		}else if(pageNum.equals("11")){
			modelMap.addAllAttributes(svc.getReviewList(req, paramMap));
			return "jsonView";

		}else if(pageNum.equals("20")){
			// reviewSeq가 없으면 목록으로 리다이렉트
			if(paramMap.get("reviewSeq") == null || paramMap.get("reviewSeq").toString().isEmpty()) {
				return "redirect:/apage/review010.do";
			}
			modelMap.addAllAttributes(svc.getReviewView(req, paramMap));

		}else if(pageNum.equals("30")){
			modelMap.addAttribute("svcCdList", CmmnSvc.getServiceCdList());

		}else if(pageNum.equals("31")){
			modelMap.addAllAttributes(svc.setReviewReg(req, paramMap));
			return "jsonView";

		}else if(pageNum.equals("40")){
			// reviewSeq가 없으면 목록으로 리다이렉트
			if(paramMap.get("reviewSeq") == null || paramMap.get("reviewSeq").toString().isEmpty()) {
				return "redirect:/apage/review010.do";
			}
			modelMap.addAllAttributes(svc.getReviewView(req, paramMap));

		}else if(pageNum.equals("41")){
			modelMap.addAllAttributes(svc.setReviewUpd(req, paramMap));
			return "jsonView";

		}else if(pageNum.equals("42")){	// 노출 변경
			modelMap.addAllAttributes(svc.setReviewDispUpd(req, paramMap));
			return "jsonView";

		}else if(pageNum.equals("43")){	// 일괄 노출 변경
			modelMap.addAllAttributes(svc.setReviewDispUpdAll(req, paramMap));
			return "jsonView";

		}else if(pageNum.equals("51")){
			modelMap.addAllAttributes(svc.setReviewDel(req, paramMap));
			return "jsonView";
		}

		StringBuilder re = new StringBuilder();
		re.append("apage/review/review0");
		re.append(pageNumber);

		return re.toString();
	}

	@PostMapping("/reviewList.do")
	public String reviewList(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getReviewList(req, paramMap));

		return "jsonView";
	}

	@PostMapping("/reviewView.do")
	public String reviewView(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.getReviewView(req, paramMap));

		return "jsonView";
	}

	@PostMapping("/reviewReg.do")
	public String reviewReg(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setReviewReg(req, paramMap));

		return "jsonView";
	}

	@PostMapping("/reviewUpd.do")
	public String reviewUpd(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setReviewUpd(req, paramMap));

		return "jsonView";
	}

	@PostMapping("/reviewDel.do")
	public String reviewDel(HttpServletRequest req, HttpServletResponse res, @RequestParam HashMap<String,Object> paramMap, ModelMap modelMap) throws Exception {

		modelMap.addAllAttributes(svc.setReviewDel(req, paramMap));

		return "jsonView";
	}

	// ======================== REST API Endpoints (JSON Response) ========================

	/**
	 * REST API: 모든 작업후기 데이터 조회 (페이징 없이)
	 * 주의: 이 메소드는 @GetMapping("/api/reviews/{reviewSeq}") 보다 먼저 정의되어야 함
	 */
	@GetMapping("/api/reviews/all")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getAllReviewsApi(
			HttpServletRequest req,
			@RequestParam(required = false) String dispYn,
			@RequestParam(defaultValue = "20") int limit) {

		try {
			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("limitStartNum", 0);
			paramMap.put("limitViewRowCnt", limit); // 기본 20개 반환
			paramMap.put("dispYn", dispYn != null ? dispYn : "Y"); // 기본적으로 노출되는 것만

			Map<String, Object> result = svc.getReviewList(req, paramMap);

			// API 응답 형식으로 변환
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data"				,result.get("list"));
			response.put("totalCount"		,result.get("rowNum"));
			response.put("serviceCdList"	,result.get("serviceCdLst"));
			response.put("productCdList"	,result.get("productCdList"));

			return ResponseEntity.ok(response);

		} catch (Exception e) {
			log.error("Error in getAllReviewsApi", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "전체 리뷰 조회 중 오류가 발생했습니다.");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	/**
	 * REST API: 작업후기 등록 (JSON 요청/응답)
	 */
	@Operation(summary = "리뷰 등록", description = "새로운 리뷰를 등록합니다. 비밀번호 설정이 가능합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "등록 성공"),
		@ApiResponse(responseCode = "400", description = "잘못된 요청")
	})
	@PostMapping("/api/reviews")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createReviewApi(
			HttpServletRequest req,
			@RequestBody Map<String, Object> requestBody) {

		try {
			// 필수 필드 검증
			if (requestBody.get("reviewNm") == null || requestBody.get("reviewNm").toString().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("작성자명은 필수입니다."));
			}
			if (requestBody.get("reviewCn") == null || requestBody.get("reviewCn").toString().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("후기 내용은 필수입니다."));
			}
			if (requestBody.get("starRate") == null) {
				return ResponseEntity.badRequest().body(createErrorResponse("별점은 필수입니다."));
			}

			// 별점 유효성 검증
			int starRate = Integer.parseInt(requestBody.get("starRate").toString());
			if (starRate < 1 || starRate > 5) {
				return ResponseEntity.badRequest().body(createErrorResponse("별점은 1-5 사이의 값이어야 합니다."));
			}

			// 기본값 설정
			if (requestBody.get("dispYn") == null) {
				requestBody.put("dispYn", "Y");
			}

			HashMap<String, Object> paramMap = new HashMap<>(requestBody);
			Map<String, Object> result = svc.setReviewReg(req, paramMap);

			// API 응답 형식으로 변환
			Map<String, Object> response = new HashMap<>();
			response.put("success", "Y".equals(result.get("isReg")));
			response.put("reviewSeq", result.get("reviewSeq"));
			response.put("message", "Y".equals(result.get("isReg")) ? "리뷰가 성공적으로 등록되었습니다." : "리뷰 등록에 실패했습니다.");

			if ("Y".equals(result.get("isReg"))) {
				return ResponseEntity.status(HttpStatus.CREATED).body(response);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (Exception e) {
			log.error("Error in createReviewApi", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse("리뷰 등록 중 오류가 발생했습니다."));
		}
	}

	/**
	 * REST API: 작업후기 수정 (JSON 요청/응답)
	 * 비밀번호가 설정된 리뷰의 경우 비밀번호 검증 후 수정 진행
	 * 비밀번호 검증 성공 시 true, 실패 시 false 반환
	 */
	@Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다. 비밀번호가 설정된 경우 pw 필드에 비밀번호를 포함해야 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "수정 성공 (success: true, passwordValid: true)"),
		@ApiResponse(responseCode = "401", description = "비밀번호 필요 (requirePassword: true)"),
		@ApiResponse(responseCode = "403", description = "잘못된 비밀번호 (success: false, passwordValid: false)"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
	})
	@PostMapping("/api/reviews/{reviewSeq}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateReviewApi(HttpServletRequest req,@PathVariable int reviewSeq,@RequestBody Map<String, Object> requestBody) {

		try {
			log.info("[API 호출] POST /api/reviews/{} - 비밀번호 제공 여부: {}", 	reviewSeq, requestBody.get("pw") != null ? "있음" : "없음");

			requestBody.put("reviewSeq", reviewSeq);
			HashMap<String, Object> paramMap = new HashMap<>(requestBody);

			// 리뷰 수정 서비스 호출 (비밀번호 검증 포함)
			Map<String, Object> result = svc.setReviewUpd(req, paramMap);

			// API 응답 형식으로 변환
			Map<String, Object> response = new HashMap<>();

			// success 필드가 있으면 그 값 사용, 없으면 isUpd로 판단
			boolean success = Boolean.TRUE.equals(result.get("success")) || "Y".equals(result.get("isUpd"));
			response.put("success", success);

			// 비밀번호 검증 결과 추가
			if (result.get("passwordValid") != null) {
				response.put("passwordValid", result.get("passwordValid"));
			}

			// 비밀번호가 필요한 경우 표시
			if (Boolean.TRUE.equals(result.get("requirePassword"))) {
				response.put("requirePassword", true);
			}

			// 메시지 설정
			response.put("message", result.get("message"));

			// HTTP 상태 코드 결정
			if (success) {
				// 수정 성공
				log.info("[API] 리뷰 수정 성공 - reviewSeq: {}, passwordValid: {}",
					reviewSeq, result.get("passwordValid"));
				return ResponseEntity.ok(response);
			} else if (Boolean.FALSE.equals(result.get("passwordValid"))) {
				// 비밀번호 검증 실패
				log.warn("[API] 리뷰 작업 실패 - reviewSeq: {}, 이유: 비밀번호 검증 실패", reviewSeq);
				if (Boolean.TRUE.equals(result.get("requirePassword"))) {
					// 비밀번호가 필요한데 제공되지 않음
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
				} else {
					// 잘못된 비밀번호
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
				}
			} else if ("존재하지 않는 리뷰입니다.".equals(result.get("message"))) {
				// 리뷰를 찾을 수 없음
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} else {
				// 기타 실패
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (Exception e) {
			log.error("Error in updateReviewApi", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("passwordValid", false);
			errorResponse.put("message", "리뷰 수정 중 오류가 발생했습니다.");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	/**
	 * REST API: 작업후기 삭제 (JSON 응답)
	 * 비밀번호가 설정된 리뷰의 경우 비밀번호 검증 후 삭제 진행
	 * 비밀번호 검증 성공 시 true, 실패 시 false 반환
	 */
	@Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다. 비밀번호가 설정된 경우 pw 필드에 비밀번호를 포함해야 합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "삭제 성공 (success: true, passwordValid: true)"),
		@ApiResponse(responseCode = "401", description = "비밀번호 필요 (requirePassword: true)"),
		@ApiResponse(responseCode = "403", description = "잘못된 비밀번호 (success: false, passwordValid: false)"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
	})
	@PostMapping("/api/reviews/{reviewSeq}/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteReviewApi(
			HttpServletRequest req,
			@PathVariable int reviewSeq,
			@RequestBody(required = false) Map<String, Object> requestBody) {

		try {
			log.info("[API 호출] POST /api/reviews/{}/delete - 비밀번호 제공 여부: {}",
				reviewSeq, (requestBody != null && requestBody.get("pw") != null) ? "있음" : "없음");

			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("reviewSeq", reviewSeq);

			// 비밀번호가 있으면 추가
			if (requestBody != null && requestBody.get("pw") != null) {
				log.debug("[API] 비밀번호 파라미터 추가");
				paramMap.put("pw", requestBody.get("pw"));
			}

			// 리뷰 삭제 서비스 호출 (비밀번호 검증 포함)
			Map<String, Object> result = svc.setReviewDel(req, paramMap);

			// API 응답 형식으로 변환
			Map<String, Object> response = new HashMap<>();

			// success 필드가 있으면 그 값 사용, 없으면 isDel로 판단
			boolean success = Boolean.TRUE.equals(result.get("success")) || "Y".equals(result.get("isDel"));
			response.put("success", success);

			// 비밀번호 검증 결과 추가
			if (result.get("passwordValid") != null) {
				response.put("passwordValid", result.get("passwordValid"));
			}

			// 비밀번호가 필요한 경우 표시
			if (Boolean.TRUE.equals(result.get("requirePassword"))) {
				response.put("requirePassword", true);
			}

			// 메시지 설정
			response.put("message", result.get("message"));

			// HTTP 상태 코드 결정
			if (success) {
				// 삭제 성공
				log.info("[API] 리뷰 삭제 성공 - reviewSeq: {}, passwordValid: {}",
					reviewSeq, result.get("passwordValid"));
				return ResponseEntity.ok(response);
			} else if (Boolean.FALSE.equals(result.get("passwordValid"))) {
				// 비밀번호 검증 실패
				log.warn("[API] 리뷰 작업 실패 - reviewSeq: {}, 이유: 비밀번호 검증 실패", reviewSeq);
				if (Boolean.TRUE.equals(result.get("requirePassword"))) {
					// 비밀번호가 필요한데 제공되지 않음
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
				} else {
					// 잘못된 비밀번호
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
				}
			} else if ("존재하지 않는 리뷰입니다.".equals(result.get("message"))) {
				// 리뷰를 찾을 수 없음
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			} else {
				// 기타 실패
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (Exception e) {
			log.error("Error in deleteReviewApi", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("passwordValid", false);
			errorResponse.put("message", "리뷰 삭제 중 오류가 발생했습니다.");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	private Map<String, Object> createErrorResponse(String message) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", message);
		return response;
	}

	/**
	 * *** 사용하지 않음 ***
	 * REST API: 작업후기 목록 조회 (JSON 응답)
	 * 프론트엔드에서 직접 호출 가능
	 */
	@Operation(summary = "리뷰 목록 조회", description = "작업후기 목록을 페이징하여 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "500", description = "서버 오류")
	})
	@GetMapping("/api/reviews")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getReviewListApi(
			HttpServletRequest req,
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false) String reviewNm,
			@RequestParam(required = false) String serviceCd,
			@RequestParam(required = false) Integer starRate,
			@RequestParam(required = false) String dispYn) {

		try {
			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("pageNum", pageNum);
			paramMap.put("limitViewRowCnt", pageSize);
			paramMap.put("reviewNm", reviewNm);
			paramMap.put("serviceCd", serviceCd);
			paramMap.put("starRate", starRate);
			paramMap.put("dispYn", dispYn);

			// 페이지 계산
			int limitStartNum = (pageNum - 1) * pageSize;
			paramMap.put("limitStartNum", limitStartNum);

			Map<String, Object> result = svc.getReviewList(req, paramMap);

			// API 응답 형식으로 변환
			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", result.get("list"));
			response.put("pagination", result.get("pagination"));
			response.put("totalCount", result.get("rowNum"));
			response.put("currentPage", pageNum);
			response.put("pageSize", pageSize);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error in getReviewListApi", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "리뷰 목록 조회 중 오류가 발생했습니다.");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	/**
	 * *** 사용 하지 않음 ***
	 * REST API: 작업후기 상세 조회 (JSON 응답)
	 * 비밀번호가 설정된 리뷰의 경우 비밀번호 검증 필요
	 */
	@Operation(summary = "리뷰 상세 조회", description = "특정 리뷰의 상세 정보를 조회합니다. 비밀번호가 설정된 경우 password 파라미터가 필요합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공"),
		@ApiResponse(responseCode = "401", description = "비밀번호 필요"),
		@ApiResponse(responseCode = "403", description = "잘못된 비밀번호"),
		@ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
	})
	@SecurityRequirement(name = "reviewPassword")
	@GetMapping("/api/reviews/{reviewSeq}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getReviewDetailApi(
			HttpServletRequest req,
			@PathVariable int reviewSeq,
			@RequestParam(required = false) String password) {

		try {
			log.info("[API 호출] GET /api/reviews/{} - 비밀번호 제공 여부: {}",
				reviewSeq, password != null ? "있음" : "없음");

			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("reviewSeq", reviewSeq);

			// 비밀번호가 제공된 경우 추가
			if (password != null && !password.trim().isEmpty()) {
				log.debug("[API] 비밀번호 파라미터 추가 - 길이: {}", password.length());
				paramMap.put("pw", password);
			}

			// 비밀번호 검증이 포함된 조회 메서드 호출
			Map<String, Object> result = svc.getReviewViewWithPasswordCheck(req, paramMap);

			// 성공 여부 확인
			if (Boolean.FALSE.equals(result.get("success"))) {
				log.warn("[API] 리뷰 조회 실패 - reviewSeq: {}, 이유: {}",
					reviewSeq, result.get("message"));

				Map<String, Object> response = new HashMap<>();
				response.put("success", false);
				response.put("message", result.get("message"));

				// 비밀번호가 필요한 경우 표시
				if (Boolean.TRUE.equals(result.get("requirePassword"))) {
					response.put("requirePassword", true);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
				}

				// 비밀번호 오류 또는 리뷰를 찾을 수 없는 경우
				if ("존재하지 않는 리뷰입니다.".equals(result.get("message"))) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
				}
			}

			// API 응답 형식으로 변환
			log.info("[API] 리뷰 조회 성공 - reviewSeq: {}", reviewSeq);

			Map<String, Object> response = new HashMap<>();
			response.put("success", true);
			response.put("data", result.get("view"));
			response.put("serviceCdList", result.get("serviceCdLst"));

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error in getReviewDetailApi", e);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("message", "리뷰 상세 조회 중 오류가 발생했습니다.");
			errorResponse.put("error", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}