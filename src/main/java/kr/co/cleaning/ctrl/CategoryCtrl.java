package kr.co.cleaning.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.cleaning.svc.CategorySvc;

/**
 * 서비스 카테고리 관리 Controller
 */
@Controller
public class CategoryCtrl {

    @Autowired
    private CategorySvc categorySvc;

    /**
     * 서비스 카테고리 관리 화면 (목록/상세/등록/수정)
     * 010 : 목록
     * 020 : 상세
     * 030 : 등록
     * 040 : 수정
     */
    @RequestMapping("/apage/category0{pageNum:[0-9]+}.do")
    public String serviceCategory(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = false) Map<String, Object> params, @PathVariable String pageNum, ModelMap model) throws Exception {

        // params가 null인 경우 초기화
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        String pageNumber = pageNum;

        if (pageNum.equals("11")) {
            // 목록 데이터 조회 (AJAX)
            model.addAllAttributes(categorySvc.getCategoryList(params));
            return "jsonView";

        } else if (pageNum.equals("21")) {
            // 상세 데이터 조회
            String serviceCd = (String) params.get("serviceCd");
            if (serviceCd != null && !"".equals(serviceCd)) {
                Map<String, Object> detail = categorySvc.getServiceCategory(serviceCd);
                model.put("detail", detail);
            }
            pageNumber = "20";

        } else if (pageNum.equals("31")) {
            // 등록 처리
            Map<String, Object> resultMap = categorySvc.insertServiceCategory(params);
            model.put("result", resultMap);
            return "jsonView";

        } else if (pageNum.equals("41")) {
            // 수정 처리
            Map<String, Object> resultMap = categorySvc.updateServiceCategory(params);
            model.put("result", resultMap);
            return "jsonView";

        } else if (pageNum.equals("51")) {
            // 삭제 처리
            String serviceCd = (String) params.get("serviceCd");
            Map<String, Object> resultMap = categorySvc.deleteServiceCategory(serviceCd);
            model.putAll(resultMap);
            return "jsonView";

        } else if (pageNum.equals("10")) {
            // 목록 화면 - 초기 데이터 로드
            if (params.get("pageNo") == null) {
                params.put("pageNo", "1");
            }
            Map<String, Object> resultMap = categorySvc.getCategoryList(params);
            if (resultMap == null) {
                resultMap = new HashMap<>();
            }
            model.put("resultMap", resultMap);
            model.put("params", params);
            pageNumber = "10";

        } else if (pageNum.equals("20")) {
            // 상세 화면
            String serviceCd = (String) params.get("serviceCd");
            if (serviceCd != null && !"".equals(serviceCd)) {
                Map<String, Object> detail = categorySvc.getServiceCategory(serviceCd);
                model.put("detail", detail);
            }
            model.put("params", params);
            pageNumber = "20";

        } else if (pageNum.equals("30")) {
            // 등록 화면
            model.put("params", params);
            pageNumber = "30";

        } else if (pageNum.equals("40")) {
            // 수정 화면
            String serviceCd = (String) params.get("serviceCd");
            if (serviceCd != null && !"".equals(serviceCd)) {
                Map<String, Object> detail = categorySvc.getServiceCategory(serviceCd);
                model.put("detail", detail);
            }
            model.put("params", params);
            pageNumber = "40";

        } else {
            // 기본은 목록 화면
            if (params.get("pageNo") == null) {
                params.put("pageNo", "1");
            }
            Map<String, Object> resultMap = categorySvc.getCategoryList(params);
            if (resultMap == null) {
                resultMap = new HashMap<>();
            }
            model.put("resultMap", resultMap);
            model.put("params", params);
            pageNumber = "10";
        }

        StringBuilder re = new StringBuilder();
        re.append("apage/category/category0");
        re.append(pageNumber);

        return re.toString();
    }

    /**
     * 활성화된 서비스 카테고리 목록 조회 (콤보박스용)
     */
    @PostMapping("/categoryCombo.do")
    @ResponseBody
    public Map<String, Object> getActiveServiceCategoryList() {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            List<Map<String, Object>> list = categorySvc.getActiveServiceCategoryList();
            resultMap.put("result", "SUCCESS");
            resultMap.put("list", list);
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "서비스 카테고리 목록 조회 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }

    /**
     * 서비스 카테고리 일괄 수정
     */
    @PostMapping("/serviceCategoryBatch.do")
    @ResponseBody
    public Map<String, Object> updateCategoryBatch(@RequestBody List<Map<String, Object>> list) {
        return categorySvc.updateCategoryBatch(list);
    }
}