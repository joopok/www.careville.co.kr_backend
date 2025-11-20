package kr.co.cleaning.ctrl;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import kr.co.cleaning.svc.ProductSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 상품 관련 공개 API 컨트롤러
 * 카테고리와 상품 정보를 제공하는 RESTful API
 */
@RestController
public class ProductApiCtrl {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductApiCtrl.class);
    
    @Autowired
    private ProductSvc productSvc;
    
    /**
     * 통합 카테고리-상품 조회 API
     * 카테고리 목록과 상품 목록을 한 번의 호출로 조회
     * 
     * @param param 조회 파라미터 (groupByCategory, includeStats, onlyActive)
     * @return 카테고리 및 상품 정보
     */
    @GetMapping("/api/v1/category-products.do")
    public Map<String, Object> getCategoryAndProducts(@RequestParam(required = false) Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (param == null) {
                param = new HashMap<>();
            }
            
            // 파라미터 기본값 설정
            boolean groupByCategory = Boolean.parseBoolean(String.valueOf(param.getOrDefault("groupByCategory", false)));
            boolean includeStats = Boolean.parseBoolean(String.valueOf(param.getOrDefault("includeStats", true)));
            boolean onlyActive = Boolean.parseBoolean(String.valueOf(param.getOrDefault("onlyActive", true)));
            
            logger.info("API 호출 파라미터: groupByCategory={}, includeStats={}, onlyActive={}", 
                       groupByCategory, includeStats, onlyActive);
            
            // 카테고리 목록 조회
            List<Map<String, Object>> categories = productSvc.getCategoryList();
            
            // 상품 목록 조회 파라미터 설정
            Map<String, Object> productParam = new HashMap<>();
            if (onlyActive) {
                productParam.put("displayYn", "Y");
                productParam.put("saleYn", "Y");
            }
            
            // 상품 목록 조회
            List<Map<String, Object>> products = productSvc.getProductWithCategory(productParam);
            
            if (groupByCategory) {
                // 카테고리별로 상품 그룹핑
                Map<String, List<Map<String, Object>>> productsByCategory = new HashMap<>();
                for (Map<String, Object> product : products) {
                    String serviceCd = String.valueOf(product.get("serviceCd"));
                    productsByCategory.computeIfAbsent(serviceCd, k -> new ArrayList<>()).add(product);
                }
                
                // 카테고리에 상품 추가
                for (Map<String, Object> category : categories) {
                    String serviceCd = String.valueOf(category.get("serviceCd"));
                    List<Map<String, Object>> categoryProducts = productsByCategory.getOrDefault(serviceCd, new ArrayList<>());
                    category.put("products", categoryProducts);
                    category.put("productCount", categoryProducts.size());
                }
                
                result.put("categories", categories);
            } else {
                // 카테고리와 상품 별도 반환
                result.put("categories", categories);
                result.put("products", products);
            }
            
            // 통계 정보 추가
            if (includeStats) {
                Map<String, Object> statistics = new HashMap<>();
                statistics.put("totalCategories", categories.size());
                statistics.put("totalProducts", products.size());
                
                // 카테고리별 상품 수
                Map<String, Integer> productsByCategory = new HashMap<>();
                for (Map<String, Object> product : products) {
                    String serviceCd = String.valueOf(product.get("serviceCd"));
                    productsByCategory.merge(serviceCd, 1, Integer::sum);
                }
                statistics.put("productsByCategory", productsByCategory);
                
                // 가격 통계
                if (!products.isEmpty()) {
                    int minPrice = Integer.MAX_VALUE;
                    int maxPrice = 0;
                    int totalPrice = 0;
                    int count = 0;
                    
                    for (Map<String, Object> product : products) {
                        Object salePriceObj = product.get("salePrice");
                        if (salePriceObj != null) {
                            int price = Integer.parseInt(String.valueOf(salePriceObj));
                            minPrice = Math.min(minPrice, price);
                            maxPrice = Math.max(maxPrice, price);
                            totalPrice += price;
                            count++;
                        }
                    }
                    
                    if (count > 0) {
                        Map<String, Object> priceRange = new HashMap<>();
                        priceRange.put("min", minPrice);
                        priceRange.put("max", maxPrice);
                        priceRange.put("average", totalPrice / count);
                        statistics.put("priceRange", priceRange);
                    }
                }
                
                result.put("statistics", statistics);
            }
            
            // 응답 파라미터 정보
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("groupByCategory", groupByCategory);
            parameters.put("includeStats", includeStats);
            parameters.put("onlyActive", onlyActive);
            result.put("parameters", parameters);
            
            result.put("result", "SUCCESS");
            result.put("timestamp", new Date());
            
        } catch (Exception e) {
            logger.error("카테고리 및 상품 조회 실패", e);
            result.put("result", "FAIL");
            result.put("msg", "카테고리 및 상품 조회에 실패했습니다: " + e.getMessage());
            result.put("timestamp", new Date());
        }
        
        return result;
    }
    
    /**
     * 심플 카탈로그 API
     * 간단한 형태로 카테고리와 상품 목록 반환
     * 
     * @return 카탈로그 정보
     */
    @GetMapping("/api/v1/catalog")
    public Map<String, Object> getCatalog() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 활성화된 카테고리 조회
            List<Map<String, Object>> categories = productSvc.getCategoryList();
            
            // 활성화된 상품 조회
            Map<String, Object> productParam = new HashMap<>();
            productParam.put("displayYn", "Y");
            productParam.put("saleYn", "Y");
            List<Map<String, Object>> products = productSvc.getProductWithCategory(productParam);
            
            // 응답 데이터 구성
            Map<String, Object> data = new HashMap<>();
            data.put("categories", categories);
            data.put("products", products);
            
            // 요약 정보
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalCategories", categories.size());
            summary.put("totalProducts", products.size());
            data.put("summary", summary);
            
            result.put("success", true);
            result.put("data", data);
            
        } catch (Exception e) {
            logger.error("카탈로그 조회 실패", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}