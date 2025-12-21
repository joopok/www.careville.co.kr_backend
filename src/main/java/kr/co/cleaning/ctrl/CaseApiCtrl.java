package kr.co.cleaning.ctrl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.cleaning.svc.CaseSvc;

/**
 * 시공사례 공개 API 컨트롤러
 * 시공사례 목록 및 상세 정보를 제공하는 RESTful API
 */
@RestController
@RequestMapping("/api/cases")
@Tag(name = "시공사례 API", description = "시공사례 조회 API")
public class CaseApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(CaseApiCtrl.class);

    @Autowired
    private CaseSvc caseSvc;

    /**
     * 시공사례 목록 조회 API
     * 등록일 기준 내림차순으로 정렬된 목록 반환
     *
     * @param serviceCd 서비스 코드 (선택)
     * @param limit 조회 건수 제한 (선택)
     * @return 시공사례 목록
     */
    @GetMapping
    @Operation(summary = "시공사례 목록 조회", description = "등록일 기준 내림차순으로 시공사례 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public Map<String, Object> getCaseList(
            @Parameter(description = "서비스 코드") @RequestParam(required = false) String serviceCd,
            @Parameter(description = "조회 건수 제한") @RequestParam(required = false) Integer limit) {

        Map<String, Object> result = new HashMap<>();

        try {
            HashMap<String, Object> paramMap = new HashMap<>();

            if (serviceCd != null && !serviceCd.isEmpty()) {
                paramMap.put("serviceCd", serviceCd);
            }
            if (limit != null && limit > 0) {
                paramMap.put("limit", limit);
            }

            logger.info("시공사례 목록 API 호출 - serviceCd: {}, limit: {}", serviceCd, limit);

            List<HashMap<String, Object>> list = caseSvc.getCaseListForApi(paramMap);

            result.put("success", true);
            result.put("data", list);
            result.put("count", list.size());
            result.put("timestamp", new Date());

        } catch (Exception e) {
            logger.error("시공사례 목록 조회 실패", e);
            result.put("success", false);
            result.put("error", "시공사례 목록 조회에 실패했습니다: " + e.getMessage());
            result.put("timestamp", new Date());
        }

        return result;
    }

    /**
     * 시공사례 상세 조회 API
     *
     * @param caseSeq 시공사례 일련번호
     * @return 시공사례 상세 정보
     */
    @GetMapping("/{caseSeq}")
    @Operation(summary = "시공사례 상세 조회", description = "시공사례 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "시공사례를 찾을 수 없음")
    })
    public Map<String, Object> getCaseView(
            @Parameter(description = "시공사례 일련번호", required = true) @PathVariable String caseSeq) {

        Map<String, Object> result = new HashMap<>();

        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("caseSeq", caseSeq);

            logger.info("시공사례 상세 API 호출 - caseSeq: {}", caseSeq);

            HashMap<String, Object> view = caseSvc.getCaseViewForApi(paramMap);

            if (view == null) {
                result.put("success", false);
                result.put("error", "시공사례를 찾을 수 없습니다.");
                result.put("timestamp", new Date());
                return result;
            }

            result.put("success", true);
            result.put("data", view);
            result.put("timestamp", new Date());

        } catch (Exception e) {
            logger.error("시공사례 상세 조회 실패", e);
            result.put("success", false);
            result.put("error", "시공사례 상세 조회에 실패했습니다: " + e.getMessage());
            result.put("timestamp", new Date());
        }

        return result;
    }
}
