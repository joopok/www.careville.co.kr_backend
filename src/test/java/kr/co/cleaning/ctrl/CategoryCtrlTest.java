package kr.co.cleaning.ctrl;

import kr.co.cleaning.svc.CategorySvc;
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

@WebMvcTest(CategoryCtrl.class)
class CategoryCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategorySvc categorySvc;

    @Test
    @DisplayName("카테고리 목록 화면 이동")
    void categoryListView() throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("list", List.of());
        result.put("pagination", Map.of());
        when(categorySvc.getCategoryList(any())).thenReturn(result);

        mockMvc.perform(get("/apage/category010.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/category/category010"))
                .andExpect(model().attributeExists("resultMap"))
                .andExpect(model().attributeExists("params"));
    }

    @Test
    @DisplayName("카테고리 상세 화면 이동")
    void categoryDetailView() throws Exception {
        when(categorySvc.getServiceCategory("SV001")).thenReturn(Map.of("serviceCd", "SV001"));

        mockMvc.perform(get("/apage/category020.do").param("serviceCd", "SV001"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/category/category020"))
                .andExpect(model().attributeExists("detail"));
    }

    @Test
    @DisplayName("카테고리 목록 JSON(11)")
    void categoryListJson() throws Exception {
        when(categorySvc.getCategoryList(any())).thenReturn(Map.of("list", List.of(Map.of("serviceCd", "SV001"))));

        mockMvc.perform(get("/apage/category011.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    @DisplayName("카테고리 등록 JSON(31)")
    void categoryInsertJson() throws Exception {
        when(categorySvc.insertServiceCategory(any())).thenReturn(Map.of("success", true));

        mockMvc.perform(get("/apage/category031.do").param("serviceNm", "청소"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("result"));
    }

    @Test
    @DisplayName("카테고리 삭제 JSON(51)")
    void categoryDeleteJson() throws Exception {
        when(categorySvc.deleteServiceCategory("SV001")).thenReturn(Map.of("result", "SUCCESS"));

        mockMvc.perform(get("/apage/category051.do").param("serviceCd", "SV001"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("result"));
    }

    @Test
    @DisplayName("활성 카테고리 콤보 JSON")
    void categoryCombo() throws Exception {
        when(categorySvc.getActiveServiceCategoryList()).thenReturn(List.of(Map.of("serviceCd", "SV001")));

        mockMvc.perform(post("/categoryCombo.do"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("SUCCESS"))
                .andExpect(jsonPath("$.list").isArray());
    }

    @Test
    @DisplayName("서비스 카테고리 일괄 수정")
    void serviceCategoryBatch() throws Exception {
        when(categorySvc.updateCategoryBatch(any())).thenReturn(Map.of("updated", 2));

        String body = "[{\"serviceCd\":\"SV001\"},{\"serviceCd\":\"SV002\"}]";
        mockMvc.perform(post("/serviceCategoryBatch.do")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(2));
    }
}

