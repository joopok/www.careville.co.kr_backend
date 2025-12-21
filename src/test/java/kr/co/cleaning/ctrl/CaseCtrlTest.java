package kr.co.cleaning.ctrl;

import kr.co.cleaning.svc.CaseSvc;
import kr.co.cleaning.svc.CmmnSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CaseCtrl.class)
class CaseCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CaseSvc caseSvc;

    @MockBean
    private CmmnSvc cmmnSvc;

    @Test
    @DisplayName("사례 목록 화면 이동(10)")
    void caseListView() throws Exception {
        when(caseSvc.getCaseList(any(), any())).thenReturn(new HashMap<>(Map.of("list", List.of())));

        mockMvc.perform(get("/apage/case010.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/case/case010"));
    }

    @Test
    @DisplayName("사례 상세 화면 이동(20)")
    void caseDetailView() throws Exception {
        when(caseSvc.getCaseView(any(), any())).thenReturn(new HashMap<>(Map.of("view", Map.of("caseSeq", 1))));

        mockMvc.perform(get("/apage/case020.do").param("caseSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/case/case020"))
                .andExpect(model().attributeExists("view"));
    }

    @Test
    @DisplayName("사례 등록 화면 이동(30)")
    void caseRegisterView() throws Exception {
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());

        mockMvc.perform(get("/apage/case030.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/case/case030"))
                .andExpect(model().attributeExists("svcCdList"));
    }

    @Test
    @DisplayName("사례 등록 JSON(31)")
    void caseRegisterJson() throws Exception {
        when(caseSvc.setCaseReg(any(), any())).thenReturn(new HashMap<>(Map.of("isReg", "Y")));

        mockMvc.perform(get("/apage/case031.do").param("title", "등록"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("isReg"));
    }

    @Test
    @DisplayName("사례 수정 JSON(41)")
    void caseUpdateJson() throws Exception {
        when(caseSvc.setCaseUpd(any(), any())).thenReturn(new HashMap<>(Map.of("isUpd", "Y")));

        mockMvc.perform(get("/apage/case041.do").param("caseSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("isUpd"));
    }

    @Test
    @DisplayName("사례 삭제 JSON(51)")
    void caseDeleteJson() throws Exception {
        when(caseSvc.setCaseDel(any(), any())).thenReturn(new HashMap<>(Map.of("isDel", "Y")));

        mockMvc.perform(get("/apage/case051.do").param("caseSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("isDel"));
    }

    @Test
    @DisplayName("사례 목록/상세/등록 API JSON")
    void caseJsonApis() throws Exception {
        when(caseSvc.getCaseList(any(), any())).thenReturn(new HashMap<>(Map.of("list", List.of())));
        when(caseSvc.getCaseView(any(), any())).thenReturn(new HashMap<>(Map.of("view", Map.of())));
        when(caseSvc.setCaseReg(any(), any())).thenReturn(new HashMap<>(Map.of("isReg", "Y")));

        mockMvc.perform(post("/caseList.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/caseView.do").param("caseSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/caseReg.do").param("title", "등록"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));
    }
}

