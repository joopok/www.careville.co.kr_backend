package kr.co.cleaning.ctrl;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.svc.ProductSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductCtrl.class)
class ProductCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductSvc productSvc;

    @MockBean
    private SessionCmn sessionCmn;

    @Test
    @DisplayName("상품 목록 화면 이동 및 모델 데이터 포함")
    void productListView() throws Exception {
        Map<String, Object> svcResult = new HashMap<>();
        svcResult.put("totCnt", 1);
        svcResult.put("list", List.of(Map.of("productNo", 1)));

        when(productSvc.selectProductList(any())).thenReturn(svcResult);
        when(productSvc.selectCategoryList()).thenReturn(List.of(Map.of("categoryNo", 10)));

        mockMvc.perform(get("/apage/product010.do").param("curPage", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/product/productList"))
                .andExpect(model().attributeExists("list"))
                .andExpect(model().attributeExists("pageUtil"))
                .andExpect(model().attributeExists("categoryList"));
    }

    @Test
    @DisplayName("상품 상세 화면 이동")
    void productView() throws Exception {
        when(productSvc.selectProduct(any())).thenReturn(Map.of("productNo", 1));
        when(productSvc.selectCategoryList()).thenReturn(List.of());

        mockMvc.perform(get("/apage/product020.do").param("productNo", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/product/productView"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @DisplayName("상품 등록 화면 이동")
    void productRegisterView() throws Exception {
        when(productSvc.selectCategoryList()).thenReturn(List.of());

        mockMvc.perform(get("/apage/product030.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/product/productRegister"))
                .andExpect(model().attributeExists("categoryList"));
    }

    @Test
    @DisplayName("상품 등록 처리 SUCCESS JSON 응답")
    void productInsert() throws Exception {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("mngrId", "admin"));
        when(productSvc.insertProduct(any())).thenReturn(1);

        mockMvc.perform(post("/apage/product031.do")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productNm", "테스트상품"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attribute("result", "SUCCESS"))
                .andExpect(model().attributeExists("redirectUrl"));
    }

    @Test
    @DisplayName("상품 수정 처리 SUCCESS JSON 응답")
    void productUpdate() throws Exception {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("mngrId", "admin"));
        when(productSvc.updateProduct(any())).thenReturn(1);

        mockMvc.perform(post("/apage/product041.do")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productNo", "1")
                        .param("productNm", "수정상품"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attribute("result", "SUCCESS"))
                .andExpect(model().attributeExists("redirectUrl"));
    }

    @Test
    @DisplayName("상품 삭제 처리 SUCCESS JSON 응답")
    void productDelete() throws Exception {
        when(sessionCmn.getLogonInfo()).thenReturn(Map.of("mngrId", "admin"));
        when(productSvc.deleteProduct(any())).thenReturn(1);

        mockMvc.perform(post("/apage/product051.do")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productNo", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attribute("result", "SUCCESS"))
                .andExpect(model().attributeExists("redirectUrl"));
    }
}

