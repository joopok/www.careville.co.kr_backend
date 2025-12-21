package kr.co.cleaning.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 대시보드 통계 Mapper Interface
 */
@Mapper
public interface DashboardMapper {

    /**
     * 신규 상담 건수 조회
     */
    int getNewCnsltCnt();

    /**
     * 시공 사례 건수 조회
     */
    int getCaseCnt();

    /**
     * 고객 리뷰 건수 조회
     */
    int getReviewCnt();

    /**
     * 상품 건수 조회
     */
    int getProductCnt();

    /**
     * 최근 상담 목록 조회
     */
    List<HashMap<String, Object>> getRecentCnsltList(HashMap<String, Object> paramMap);

    /**
     * 최근 리뷰 목록 조회
     */
    List<HashMap<String, Object>> getRecentReviewList(HashMap<String, Object> paramMap);

    /**
     * 최근 시공 사례 목록 조회
     */
    List<HashMap<String, Object>> getRecentCaseList(HashMap<String, Object> paramMap);

    /**
     * 최근 상품 목록 조회
     */
    List<HashMap<String, Object>> getRecentProductList(HashMap<String, Object> paramMap);
}
