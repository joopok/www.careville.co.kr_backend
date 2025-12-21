package kr.co.cleaning.svc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.cleaning.mapper.DashboardMapper;

/**
 * 대시보드 서비스
 * 관리자 대시보드에 표시할 통계 및 최근 활동 데이터를 제공
 */
@Service
public class DashboardSvc {

    private static final Logger log = LoggerFactory.getLogger(DashboardSvc.class);

    @Autowired
    private DashboardMapper dashboardMapper;

    /**
     * 대시보드 통계 정보 조회
     * - 신규 상담 건수
     * - 시공 사례 건수
     * - 고객 리뷰 건수
     * - 상품 건수
     */
    public HashMap<String, Object> getDashboardStats() {
        HashMap<String, Object> result = new HashMap<>();

        try {
            result.put("newCnsltCnt", dashboardMapper.getNewCnsltCnt());
            result.put("caseCnt", dashboardMapper.getCaseCnt());
            result.put("reviewCnt", dashboardMapper.getReviewCnt());
            result.put("productCnt", dashboardMapper.getProductCnt());
            result.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("대시보드 통계 조회 오류: ", e);
            result.put("result", "FAIL");
            result.put("msg", "통계 정보를 불러오는 중 오류가 발생했습니다.");
        }

        return result;
    }

    /**
     * 최근 활동 목록 조회
     * - 최근 상담, 리뷰, 시공 사례, 상품 등록 정보를 통합하여 시간순으로 정렬
     */
    public HashMap<String, Object> getRecentActivities() {
        HashMap<String, Object> result = new HashMap<>();
        List<HashMap<String, Object>> activities = new ArrayList<>();

        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("limit", 3);

            // 최근 상담 목록
            List<HashMap<String, Object>> cnsltList = dashboardMapper.getRecentCnsltList(paramMap);
            for (HashMap<String, Object> item : cnsltList) {
                HashMap<String, Object> activity = new HashMap<>();
                activity.put("type", "consultation");
                activity.put("icon", "purple");
                activity.put("title", "새 상담 요청");
                activity.put("desc", item.get("nm") + "님이 " + (item.get("reqTypeNm") != null ? item.get("reqTypeNm") : "상담") + "을 요청했습니다.");
                activity.put("timeAgo", item.get("timeAgo"));
                activity.put("regDt", item.get("regDt"));
                activity.put("seq", item.get("cnsltSeq"));
                activities.add(activity);
            }

            // 최근 리뷰 목록
            List<HashMap<String, Object>> reviewList = dashboardMapper.getRecentReviewList(paramMap);
            for (HashMap<String, Object> item : reviewList) {
                HashMap<String, Object> activity = new HashMap<>();
                activity.put("type", "review");
                activity.put("icon", "amber");
                activity.put("title", "새 리뷰 등록");
                activity.put("desc", item.get("reviewNm") + "님이 별점 " + item.get("starRate") + "점 리뷰를 남겼습니다.");
                activity.put("timeAgo", item.get("timeAgo"));
                activity.put("regDt", item.get("rgsDt"));
                activity.put("seq", item.get("reviewSeq"));
                activities.add(activity);
            }

            // 최근 시공 사례 목록
            List<HashMap<String, Object>> caseList = dashboardMapper.getRecentCaseList(paramMap);
            for (HashMap<String, Object> item : caseList) {
                HashMap<String, Object> activity = new HashMap<>();
                activity.put("type", "case");
                activity.put("icon", "blue");
                activity.put("title", "시공 사례 추가");
                activity.put("desc", item.get("caseSj") + " 사례가 추가되었습니다.");
                activity.put("timeAgo", item.get("timeAgo"));
                activity.put("regDt", item.get("rgsDt"));
                activity.put("seq", item.get("caseSeq"));
                activities.add(activity);
            }

            // 시간순 정렬 (regDt 기준 내림차순)
            activities.sort((a, b) -> {
                String dateA = (String) a.get("regDt");
                String dateB = (String) b.get("regDt");
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });

            // 최근 5개만 반환
            if (activities.size() > 5) {
                activities = activities.subList(0, 5);
            }

            result.put("activities", activities);
            result.put("result", "SUCCESS");
        } catch (Exception e) {
            log.error("최근 활동 조회 오류: ", e);
            result.put("result", "FAIL");
            result.put("msg", "최근 활동 정보를 불러오는 중 오류가 발생했습니다.");
        }

        return result;
    }

    /**
     * 대시보드 전체 데이터 조회 (통계 + 최근 활동)
     */
    public HashMap<String, Object> getDashboardData() {
        HashMap<String, Object> result = new HashMap<>();

        // 통계 데이터
        HashMap<String, Object> stats = getDashboardStats();
        result.put("stats", stats);

        // 최근 활동 데이터
        HashMap<String, Object> activities = getRecentActivities();
        result.put("recentActivities", activities.get("activities"));

        result.put("result", "SUCCESS");
        return result;
    }
}
