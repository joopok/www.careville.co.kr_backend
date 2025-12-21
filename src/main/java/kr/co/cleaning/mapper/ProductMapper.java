package kr.co.cleaning.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

/**
 * 상품 관리 Mapper Interface
 */
@Mapper
public interface ProductMapper {

    // ==================== 상품 관련 ====================

    /**
     * 상품 목록 전체 건수 조회
     */
    int selectProductListCnt(Map<String, Object> param);

    /**
     * 상품 목록 조회
     */
    List<Map<String, Object>> selectProductList(Map<String, Object> param);

    /**
     * 상품 상세 조회
     */
    Map<String, Object> selectProduct(Map<String, Object> param);

    /**
     * 상품 이미지 갤러리 조회
     */
    List<Map<String, Object>> selectProductImages(Map<String, Object> param);

    /**
     * 상품 등록
     */
    int insertProduct(Map<String, Object> param);

    /**
     * 상품 수정
     */
    int updateProduct(Map<String, Object> param);

    /**
     * 상품 삭제 (논리 삭제)
     */
    int deleteProduct(Map<String, Object> param);

    /**
     * 카테고리별 상품 목록 조회 (JOIN)
     */
    List<Map<String, Object>> selectProductWithCategory(Map<String, Object> param);

    /**
     * 특정 카테고리의 상품 목록 조회
     */
    List<Map<String, Object>> selectProductByCategory(Map<String, Object> param);

    // ==================== 카테고리 관련 ====================

    /**
     * 카테고리 목록 조회
     */
    List<Map<String, Object>> selectCategoryList();

    /**
     * 카테고리 상세 조회
     */
    Map<String, Object> selectCategory(Map<String, Object> param);
}