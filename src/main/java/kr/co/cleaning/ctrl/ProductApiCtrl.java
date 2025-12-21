package kr.co.cleaning.ctrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.cleaning.svc.ProductSvc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 상품 관련 공개 API 컨트롤러
 * 카테고리와 상품 정보를 제공하는 RESTful API
 */
@Tag(name = "상품 API", description = "상품 조회 및 카테고리 관련 공개 API")
@RestController
@RequestMapping("/api")
public class ProductApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ProductApiCtrl.class);

    @Autowired
    private ProductSvc productSvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 통합 카테고리-상품 조회 API
     * 카테고리 목록과 상품 목록을 한 번의 호출로 조회
     *
     * @param param 조회 파라미터 (groupByCategory, includeStats, onlyActive)
     * @return 카테고리 및 상품 정보
     */
    @Operation(summary = "카테고리+상품 통합 조회", description = "카테고리 목록과 상품 목록을 한 번의 호출로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping({"/v1/category-products.do", "/v1/category-products"})
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
    @Operation(summary = "심플 카탈로그 조회", description = "간단한 형태로 카테고리와 상품 목록을 반환합니다.")
    @GetMapping("/v1/catalog")
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

    /**
     * 상품 목록 조회 API
     * 활성화된 상품 목록을 조회하며, JSON 필드를 파싱하여 반환
     *
     * @param serviceCd 카테고리 코드 (선택)
     * @param popularOnly 인기 상품만 조회 여부
     * @return 상품 목록
     */
    @Operation(summary = "상품 목록 조회", description = "활성화된 상품 목록을 조회합니다. JSON 필드가 파싱되어 반환됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/v1/products")
    public ResponseEntity<Map<String, Object>> getProducts(
            @Parameter(description = "카테고리 코드") @RequestParam(required = false) String serviceCd,
            @Parameter(description = "인기 상품만 조회") @RequestParam(required = false, defaultValue = "false") boolean popularOnly) {

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("displayYn", "Y");
            param.put("saleYn", "Y");
            if (serviceCd != null && !serviceCd.isEmpty()) {
                param.put("serviceCd", serviceCd);
            }

            List<Map<String, Object>> products = productSvc.getProductWithCategory(param);

            // 인기 상품만 필터링
            if (popularOnly) {
                products = products.stream()
                        .filter(p -> "Y".equals(p.get("popularYn")))
                        .toList();
            }

            // JSON 필드 파싱 및 이미지 URL 구성
            List<Map<String, Object>> processedProducts = new ArrayList<>();
            for (Map<String, Object> product : products) {
                processedProducts.add(processProductForApi(product));
            }

            result.put("success", true);
            result.put("products", processedProducts);
            result.put("totalCount", processedProducts.size());
            result.put("timestamp", new Date());

        } catch (Exception e) {
            logger.error("상품 목록 조회 실패", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 상품 상세 조회 API
     * 상품 상세 정보와 이미지 갤러리를 함께 반환
     *
     * @param productNo 상품 번호
     * @return 상품 상세 정보
     */
    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보와 이미지 갤러리를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/v1/products/{productNo}")
    public ResponseEntity<Map<String, Object>> getProductDetail(
            @Parameter(description = "상품 번호", required = true) @PathVariable int productNo) {

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("productNo", productNo);

            Map<String, Object> product = productSvc.selectProduct(param);

            if (product == null) {
                result.put("success", false);
                result.put("error", "상품을 찾을 수 없습니다.");
                return ResponseEntity.status(404).body(result);
            }

            // JSON 필드 파싱 및 이미지 URL 구성
            Map<String, Object> processedProduct = processProductForApi(product);

            // 이미지 갤러리 URL 구성
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> images = (List<Map<String, Object>>) product.get("images");
            if (images != null && !images.isEmpty()) {
                List<Map<String, Object>> processedImages = new ArrayList<>();
                for (Map<String, Object> img : images) {
                    Map<String, Object> processedImg = new HashMap<>(img);
                    processedImg.put("imageUrl", buildImageUrl(img));
                    processedImages.add(processedImg);
                }
                processedProduct.put("images", processedImages);
            }

            result.put("success", true);
            result.put("product", processedProduct);
            result.put("timestamp", new Date());

        } catch (Exception e) {
            logger.error("상품 상세 조회 실패: productNo={}", productNo, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 카테고리 목록 조회 API
     *
     * @return 카테고리 목록
     */
    @Operation(summary = "카테고리 목록 조회", description = "활성화된 카테고리 목록을 조회합니다.")
    @GetMapping("/v1/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> categories = productSvc.getCategoryList();

            result.put("success", true);
            result.put("categories", categories);
            result.put("totalCount", categories.size());

        } catch (Exception e) {
            logger.error("카테고리 목록 조회 실패", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 상품 데이터를 API 응답용으로 가공
     * - JSON 문자열 필드를 배열로 파싱
     * - 썸네일 이미지 URL 구성
     */
    private Map<String, Object> processProductForApi(Map<String, Object> product) {
        Map<String, Object> processed = new HashMap<>(product);

        // JSON 필드 파싱
        processed.put("featuresList", parseJsonArray((String) product.get("features")));
        processed.put("serviceEffectsList", parseJsonArray((String) product.get("serviceEffects")));
        processed.put("workProcessList", parseJsonArray((String) product.get("workProcess")));
        processed.put("serviceIncludesList", parseJsonArray((String) product.get("serviceIncludes")));

        // 썸네일 이미지 URL 구성
        if (product.get("thumbnailPath") != null && product.get("thumbnailNm") != null) {
            processed.put("thumbnailUrl", "/file/view/" + product.get("thumbnailSeq"));
        }

        // 평점 포맷팅
        if (product.get("avgRating") != null) {
            try {
                double rating = Double.parseDouble(product.get("avgRating").toString());
                processed.put("avgRatingFormatted", String.format("%.1f", rating));
            } catch (NumberFormatException e) {
                processed.put("avgRatingFormatted", "0.0");
            }
        } else {
            processed.put("avgRatingFormatted", "0.0");
        }

        // 가격 포맷팅
        if (product.get("originalPrice") != null) {
            processed.put("originalPriceFormatted", formatPrice(product.get("originalPrice")));
        }
        if (product.get("salePrice") != null) {
            processed.put("salePriceFormatted", formatPrice(product.get("salePrice")));
        }

        return processed;
    }

    /**
     * JSON 배열 문자열을 List로 파싱
     */
    private List<String> parseJsonArray(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            // JSON 배열인 경우
            if (jsonString.trim().startsWith("[")) {
                return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
            }
            // 줄바꿈으로 구분된 텍스트인 경우
            String[] lines = jsonString.split("\n");
            List<String> result = new ArrayList<>();
            for (String line : lines) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        } catch (Exception e) {
            logger.warn("JSON 파싱 실패: {}", jsonString, e);
            return new ArrayList<>();
        }
    }

    /**
     * 이미지 URL 구성
     */
    private String buildImageUrl(Map<String, Object> file) {
        if (file == null || file.get("fileSeq") == null) {
            return null;
        }
        return "/file/view/" + file.get("fileSeq");
    }

    /**
     * 가격 포맷팅 (천 단위 콤마)
     */
    private String formatPrice(Object price) {
        if (price == null) {
            return "0";
        }
        try {
            long priceValue = Long.parseLong(price.toString());
            return String.format("%,d", priceValue);
        } catch (NumberFormatException e) {
            return price.toString();
        }
    }
}