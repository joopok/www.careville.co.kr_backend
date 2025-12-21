package kr.co.cleaning.ctrl;

import kr.co.cleaning.svc.CmmnSvc;
import kr.co.cleaning.svc.CnsltSvc;
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

@WebMvcTest(CnsltCtrl.class)
class CnsltCtrlWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CnsltSvc cnsltSvc;

    @MockBean
    private CmmnSvc cmmnSvc;

    @Test
    @DisplayName("상담 목록 화면 이동(10)")
    void cnsltListView() throws Exception {
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());

        mockMvc.perform(get("/apage/cnslt010.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/cnslt/cnslt010"))
                .andExpect(model().attributeExists("svcCdList"));
    }

    @Test
    @DisplayName("상담 상세 화면 이동(20)")
    void cnsltDetailView() throws Exception {
        when(cnsltSvc.getCnsltView(any(), any())).thenReturn(new HashMap<>(Map.of("view", Map.of("cnsltSeq", 1))));
        when(cmmnSvc.getCodeList("002")).thenReturn(List.of());

        mockMvc.perform(get("/apage/cnslt020.do").param("cnsltSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/cnslt/cnslt020"))
                .andExpect(model().attributeExists("view"))
                .andExpect(model().attributeExists("sttusCdLst"))
                .andExpect(model().attributeExists("searchMap"));
    }

    @Test
    @DisplayName("상담 목록 JSON(11)")
    void cnsltListJson() throws Exception {
        when(cnsltSvc.getCnsltList(any(), any())).thenReturn(new HashMap<>(Map.of("list", List.of())));

        mockMvc.perform(get("/apage/cnslt011.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("list"));
    }

    @Test
    @DisplayName("상담 등록/수정 JSON API")
    void cnsltJsonApis() throws Exception {
        when(cnsltSvc.setCnsltReg(any(), any())).thenReturn(new HashMap<>(Map.of("isReg", "Y")));
        when(cnsltSvc.setCnsltUpd(any(), any())).thenReturn(new HashMap<>(Map.of("isUpd", "Y")));
        when(cnsltSvc.getCnsltView(any(), any())).thenReturn(new HashMap<>(Map.of("view", Map.of())));

        mockMvc.perform(post("/cnsltReg.do").param("nm", "홍길동"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/cnsltUpd.do").param("cnsltSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));

        mockMvc.perform(post("/cnsltView.do").param("cnsltSeq", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"));
    }
}

