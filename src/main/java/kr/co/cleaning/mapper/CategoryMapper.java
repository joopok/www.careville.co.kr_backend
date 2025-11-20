package kr.co.cleaning.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 서비스 카테고리 관리 Mapper
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 서비스 카테고리 목록 조회
     */
    List<Map<String, Object>> selectCategoryList(Map<String, Object> params);
    
    /**
     * 서비스 카테고리 목록 개수 조회
     */
    int selectCategoryListCnt(Map<String, Object> params);
    
    /**
     * 서비스 카테고리 상세 조회
     */
    Map<String, Object> selectCategory(String serviceCd);
    
    /**
     * 서비스 카테고리 등록
     */
    int insertCategory(Map<String, Object> params);
    
    /**
     * 서비스 카테고리 수정
     */
    int updateCategory(Map<String, Object> params);
    
    /**
     * 서비스 카테고리 삭제 (논리적 삭제)
     */
    int deleteCategory(Map<String, Object> params);
    
    /**
     * 서비스 코드 중복 체크
     */
    int checkDuplicateServiceCd(String serviceCd);
    
    /**
     * 최대 정렬 순서 조회
     */
    Integer selectMaxServiceOrder();
    
    /**
     * 활성화된 서비스 카테고리 목록 (드롭다운용)
     */
    List<Map<String, Object>> selectActiveServiceCategoryList();
}