package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.core.utils.PageUtil;
import kr.co.cleaning.core.utils.SUtils;
import kr.co.cleaning.mapper.CategoryMapper;

/**
 * 서비스 카테고리 관리 Service
 */
@Service
public class CategorySvc {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SessionCmn sessionCmn;

    @Autowired
    private PageUtil pageUtil;

    /**
     * 서비스 카테고리 목록 조회
     */
    public Map<String, Object> getCategoryList(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();

        // 페이징 처리
        // pageNo를 currPage로 변환
        if (params.get("pageNo") != null) {
            params.put("currPage", params.get("pageNo"));
        }

        pageUtil.setCurrPage((HashMap<String, Object>) params);
        int totalCount = categoryMapper.selectCategoryListCnt(params);
        pageUtil.setTotalRowCnt(totalCount);

        // MyBatis 쿼리용 파라미터 설정
        params.put("startIdx", params.get("limitStartNum"));
        params.put("viewRowCnt", params.get("limitViewRowCnt"));

        // 목록 조회
        List<Map<String, Object>> list = categoryMapper.selectCategoryList(params);

        resultMap.put("list", list);
        resultMap.put("pagination", pageUtil.getPaging("json"));
        resultMap.put("rowNum", pageUtil.getRowNum());
        resultMap.put("totalCount", totalCount);

        return resultMap;
    }

    /**
     * 서비스 카테고리 상세 조회
     */
    public Map<String, Object> getServiceCategory(String serviceCd) {
        return categoryMapper.selectCategory(serviceCd);
    }

    /**
     * 서비스 카테고리 등록
     */
    @Transactional
    public Map<String, Object> insertServiceCategory(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 서비스 코드 중복 체크
            String serviceCd = (String) params.get("serviceCd");
            int duplicateCount = categoryMapper.checkDuplicateServiceCd(serviceCd);

            if (duplicateCount > 0) {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "이미 존재하는 서비스 코드입니다.");
                return resultMap;
            }

            // 정렬 순서가 없으면 최대값 + 1로 설정
            if (params.get("serviceOrder") == null || "".equals(params.get("serviceOrder"))) {
                Integer maxOrder = categoryMapper.selectMaxServiceOrder();
                params.put("serviceOrder", (maxOrder != null ? maxOrder : 0) + 1);
            }

            // 사용여부 기본값 설정
            if (params.get("useYn") == null || "".equals(params.get("useYn"))) {
                params.put("useYn", "Y");
            }

            // 세션에서 사용자 ID 가져오기
            Map<String, Object> logonInfo = sessionCmn.getLogonInfo();
            String userId = logonInfo != null ? SUtils.nvl(logonInfo.get("USER_ID")) : "admin";
            params.put("regUserId", userId);

            // 등록 처리
            int result = categoryMapper.insertCategory(params);

            if (result > 0) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("msg", "서비스 카테고리가 등록되었습니다.");
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "서비스 카테고리 등록에 실패하였습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "서비스 카테고리 등록 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }

    /**
     * 서비스 카테고리 수정
     */
    @Transactional
    public Map<String, Object> updateServiceCategory(Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 세션에서 사용자 ID 가져오기
            Map<String, Object> logonInfo = sessionCmn.getLogonInfo();
            String userId = logonInfo != null ? SUtils.nvl(logonInfo.get("USER_ID")) : "admin";
            params.put("modUserId", userId);

            // 수정 처리
            int result = categoryMapper.updateCategory(params);

            if (result > 0) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("msg", "서비스 카테고리가 수정되었습니다.");
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "서비스 카테고리 수정에 실패하였습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "서비스 카테고리 수정 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }

    /**
     * 서비스 카테고리 삭제
     */
    @Transactional
    public Map<String, Object> deleteServiceCategory(String serviceCd) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // 세션에서 사용자 ID 가져오기
            Map<String, Object> logonInfo = sessionCmn.getLogonInfo();
            String userId = logonInfo != null ? SUtils.nvl(logonInfo.get("USER_ID")) : "admin";

            Map<String, Object> params = new HashMap<>();
            params.put("serviceCd", serviceCd);
            params.put("modUserId", userId);

            // 삭제 처리 (논리적 삭제)
            int result = categoryMapper.deleteCategory(params);

            if (result > 0) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("msg", "서비스 카테고리가 삭제되었습니다.");
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "서비스 카테고리 삭제에 실패하였습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "서비스 카테고리 삭제 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }

    /**
     * 활성화된 서비스 카테고리 목록 조회 (콤보박스용)
     */
    public List<Map<String, Object>> getActiveServiceCategoryList() {
        return categoryMapper.selectActiveServiceCategoryList();
    }

    /**
     * 서비스 카테고리 일괄 수정
     */
    @Transactional
    public Map<String, Object> updateCategoryBatch(List<Map<String, Object>> list) {
        Map<String, Object> resultMap = new HashMap<>();

        try {
            Map<String, Object> logonInfo = sessionCmn.getLogonInfo();
            String userId = logonInfo != null ? SUtils.nvl(logonInfo.get("USER_ID")) : "admin";

            int updateCount = 0;
            for (Map<String, Object> item : list) {
                item.put("modUserId", userId);
                updateCount += categoryMapper.updateCategory(item);
            }

            if (updateCount > 0) {
                resultMap.put("result", "SUCCESS");
                resultMap.put("msg", updateCount + "개의 서비스 카테고리가 수정되었습니다.");
                resultMap.put("updateCount", updateCount);
            } else {
                resultMap.put("result", "FAIL");
                resultMap.put("msg", "서비스 카테고리 일괄 수정에 실패하였습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("result", "FAIL");
            resultMap.put("msg", "서비스 카테고리 일괄 수정 중 오류가 발생하였습니다.");
        }

        return resultMap;
    }
}