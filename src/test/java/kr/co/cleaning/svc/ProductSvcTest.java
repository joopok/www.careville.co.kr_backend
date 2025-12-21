package kr.co.cleaning.svc;

import kr.co.cleaning.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductSvcTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductSvc productSvc;

    @Test
    @DisplayName("상품 등록 시 가격 파싱/할인율/포함사항 JSON 변환")
    void insertProduct_parsingAndTransform() {
        when(productMapper.insertProduct(any())).thenReturn(1);

        Map<String, Object> param = new HashMap<>();
        param.put("originalPrice", "200,000");
        param.put("salePrice", "150,000");
        param.put("serviceIncludes", "항목1\n항목2");

        productSvc.insertProduct(param);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(productMapper).insertProduct(captor.capture());

        Map<String, Object> used = captor.getValue();
        assertEquals("200000", used.get("originalPrice"));
        assertEquals("150000", used.get("salePrice"));
        assertEquals(25, used.get("discountRate")); // (200000-150000)/200000*100
        assertEquals("[\"항목1\",\"항목2\"]", used.get("serviceIncludes"));
    }

    @Test
    @DisplayName("상품 수정 시 숫자 파싱 및 할인율 계산")
    void updateProduct_parsingAndRate() {
        when(productMapper.updateProduct(any())).thenReturn(1);

        Map<String, Object> param = new HashMap<>();
        param.put("originalPrice", "100,000");
        param.put("salePrice", "80,000");

        productSvc.updateProduct(param);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(productMapper).updateProduct(captor.capture());

        Map<String, Object> used = captor.getValue();
        assertEquals("100000", used.get("originalPrice"));
        assertEquals("80000", used.get("salePrice"));
        assertEquals(20, used.get("discountRate"));
    }
}

