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
        Map<String, Object> product = productMapper.selectProduct(param);
        if (product != null) {
            // 이미지 갤러리 조회
            List<Map<String, Object>> images = productMapper.selectProductImages(param);
            product.put("images", images);
        }
        return product;
    }

    /**
     * 상품 이미지 갤러리 조회
     */
    public List<Map<String, Object>> selectProductImages(Map<String, Object> param) {
        return productMapper.selectProductImages(param);
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

        // JSON 필드 변환 처리
        convertToJsonArray(param, "serviceIncludes", "serviceIncludesJson");
        convertToJsonArray(param, "features", "featuresJson");
        convertToJsonArray(param, "serviceEffects", "serviceEffectsJson");
        convertToJsonArray(param, "workProcess", "workProcessJson");

        return productMapper.insertProduct(param);
    }

    /**
     * 텍스트를 JSON 배열로 변환하는 헬퍼 메서드
     */
    private void convertToJsonArray(Map<String, Object> param, String fieldName, String jsonFieldName) {
        String value = null;
        if(param.get(jsonFieldName) != null && !"".equals(param.get(jsonFieldName).toString().trim())) {
            value = param.get(jsonFieldName).toString();
        } else if(param.get(fieldName) != null && !"".equals(param.get(fieldName).toString().trim())) {
            value = param.get(fieldName).toString();
        }

        if(value != null && !value.isEmpty()) {
            if(!value.startsWith("{") && !value.startsWith("[")) {
                // 줄바꿈으로 구분된 텍스트를 JSON 배열로 변환
                String[] items = value.split("\n");
                StringBuilder json = new StringBuilder("[");
                for(int i = 0; i < items.length; i++) {
                    String item = items[i].trim();
                    if(!item.isEmpty()) {
                        if(json.length() > 1) json.append(",");
                        // JSON escape 처리
                        item = item.replace("\\", "\\\\").replace("\"", "\\\"");
                        json.append("\"").append(item).append("\"");
                    }
                }
                json.append("]");
                param.put(fieldName, json.toString());
            } else {
                param.put(fieldName, value);
            }
        }
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

        // JSON 필드 변환 처리
        convertToJsonArray(param, "serviceIncludes", "serviceIncludesJson");
        convertToJsonArray(param, "features", "featuresJson");
        convertToJsonArray(param, "serviceEffects", "serviceEffectsJson");
        convertToJsonArray(param, "workProcess", "workProcessJson");

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