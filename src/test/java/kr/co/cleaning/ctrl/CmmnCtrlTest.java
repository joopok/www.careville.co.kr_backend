package kr.co.cleaning.ctrl;

import kr.co.cleaning.core.config.SessionCmn;
import kr.co.cleaning.svc.CmmnSvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CmmnCtrl.class)
class CmmnCtrlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CmmnSvc cmmnSvc;

    @MockBean
    private SessionCmn sessionCmn;

    @Test
    @DisplayName("로그인 상태 아님 - 로그인 페이지 이동")
    void signIn_notLoggedIn() throws Exception {
        when(sessionCmn.isLogon()).thenReturn(false);

        mockMvc.perform(get("/apage"))
                .andExpect(status().isOk())
                .andExpect(view().name("apage/signIn"));
    }

    @Test
    @DisplayName("로그인 상태 - 홈으로 리다이렉트")
    void signIn_loggedIn_redirectHome() throws Exception {
        when(sessionCmn.isLogon()).thenReturn(true);

        mockMvc.perform(get("/apage"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apage/home.do"));
    }

    @Test
    @DisplayName("로그아웃 - /apage/로 리다이렉트")
    void signOut_redirect() throws Exception {
        mockMvc.perform(get("/apage/signout.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/apage/"));
    }

    @Test
    @DisplayName("공통 서비스 코드 조회 JSON 응답")
    void serviceCdList_json() throws Exception {
        when(cmmnSvc.getServiceCdList()).thenReturn(List.of());

        mockMvc.perform(post("/serviceCdList.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("jsonView"))
                .andExpect(model().attributeExists("svcCdList"));
    }
}

