package kr.co.cleaning.svc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.cleaning.mapper.ProductMapper;

/**
 * 상품 관리 Service
 */
@Service
public class ProductSvc {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 상품 목록 조회
     */
    public Map<String, Object> selectProductList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();

        // 전체 건수 조회
        int totCnt = productMapper.selectProductListCnt(param);

        // 목록 조회
        List<Map<String, Object>> list = productMapper.selectProductList(param);

        resultMap.put("totCnt", totCnt);
        resultMap.put("list", list);

        return resultMap;
    }

    /**
     * 상품 상세 조회
     */
    public Map<String, Object> selectProduct(Map<String, Object> param) {
        return productMapper.selectProduct(param);
    }

    /**
     * 상품 등록
     */
    @Transactional
    public int insertProduct(Map<String, Object> param) {

        // 가격 처리
        if(param.get("originalPrice") != null) {
            String originalPrice = param.get("originalPrice").toString().replaceAll(",", "");
            param.put("originalPrice", originalPrice);
        }

        if(param.get("salePrice") != null) {
            String salePrice = param.get("salePrice").toString().replaceAll(",", "");
            param.put("salePrice", salePrice);
        }

        // 할인율 계산
        if(param.get("originalPrice") != null && param.get("salePrice") != null) {
            try {
                int original = Integer.parseInt(param.get("originalPrice").toString());
                int sale = Integer.parseInt(param.get("salePrice").toString());
                if(original > 0) {
                    int discountRate = (int)Math.round((double)(original - sale) / original * 100);
                    param.put("discountRate", discountRate);
                }
            } catch(NumberFormatException e) {
                param.put("discountRate", 0);
            }
        }

        // 서비스 포함사항 JSON 변환
        if(param.get("serviceIncludes") != null) {
            String includes = param.get("serviceIncludes").toString();
            if(!includes.startsWith("{") && !includes.startsWith("[")) {
                // 줄바꿈으로 구분된 텍스트를 JSON 배열로 변환
                String[] items = includes.split("\n");
                StringBuilder json = new StringBuilder("[");
                for(int i = 0; i < items.length; i++) {
                    if(i > 0) json.append(",");
                    json.append("\"").append(items[i].trim()).append("\"");
                }
                json.append("]");
                param.put("serviceIncludes", json.toString());
            }
        }

        return productMapper.insertProduct(param);
    }

    /**
     * 상품 수정
     */
    @Transactional
    public int updateProduct(Map<String, Object> param) {

        // 가격 처리
        if(param.get("originalPrice") != null) {
            String originalPrice = param.get("originalPrice").toString().replaceAll(",", "");
            param.put("originalPrice", originalPrice);
        }

        if(param.get("salePrice") != null) {
            String salePrice = param.get("salePrice").toString().replaceAll(",", "");
            param.put("salePrice", salePrice);
        }

        // 할인율 계산
        if(param.get("originalPrice") != null && param.get("salePrice") != null) {
            try {
                int original = Integer.parseInt(param.get("originalPrice").toString());
                int sale = Integer.parseInt(param.get("salePrice").toString());
                if(original > 0) {
                    int discountRate = (int)Math.round((double)(original - sale) / original * 100);
                    param.put("discountRate", discountRate);
                }
            } catch(NumberFormatException e) {
                param.put("discountRate", 0);
            }
        }

        // 서비스 포함사항 JSON 변환
        if(param.get("serviceIncludes") != null) {
            String includes = param.get("serviceIncludes").toString();
            if(!includes.startsWith("{") && !includes.startsWith("[")) {
                String[] items = includes.split("\n");
                StringBuilder json = new StringBuilder("[");
                for(int i = 0; i < items.length; i++) {
                    if(i > 0) json.append(",");
                    json.append("\"").append(items[i].trim()).append("\"");
                }
                json.append("]");
                param.put("serviceIncludes", json.toString());
            }
        }

        return productMapper.updateProduct(param);
    }

    /**
     * 상품 삭제 (논리 삭제)
     */
    @Transactional
    public int deleteProduct(Map<String, Object> param) {
        return productMapper.deleteProduct(param);
    }

    /**
     * 카테고리 목록 조회
     */
    public List<Map<String, Object>> selectCategoryList() {
        return productMapper.selectCategoryList();
    }

    /**
     * 카테고리 상세 조회
     */
    public Map<String, Object> selectCategory(Map<String, Object> param) {
        return productMapper.selectCategory(param);
    }

    /**
     * 카테고리별 상품 목록 조회 (JOIN)
     */
    public List<Map<String, Object>> selectProductWithCategory(Map<String, Object> param) {
        return productMapper.selectProductWithCategory(param);
    }

    /**
     * 특정 카테고리의 상품 목록 조회
     */
    public List<Map<String, Object>> selectProductByCategory(Map<String, Object> param) {
        return productMapper.selectProductByCategory(param);
    }

    /**
     * 카테고리 목록 조회 (API용)
     */
    public List<Map<String, Object>> getCategoryList() {
        return productMapper.selectCategoryList();
    }

    /**
     * 카테고리별 상품 목록 조회 (API용)
     */
    public List<Map<String, Object>> getProductWithCategory(Map<String, Object> param) {
        return productMapper.selectProductWithCategory(param);
    }
}